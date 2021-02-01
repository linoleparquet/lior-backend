package fr.lino.layani.lior.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivities;
import fr.lino.layani.lior.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;

import fr.lino.layani.lior.dto.RoutingDto;
import fr.lino.layani.lior.model.Destination;
import fr.lino.layani.lior.model.Doctor;
import fr.lino.layani.lior.model.Establishment;

@org.springframework.stereotype.Service
public class RoutingServiceImpl implements RoutingService {

	@Autowired
	DoctorService doctorService;

	@Autowired
	DoctorRepository doctorRepository;

	final String EARLIEST_START = "09:00";
	final String LATEST_ARRIVAL = "18:00";
	final String WAITING_TIME = "00:30";
	final int MAX_DESTINATIONS_PER_DAY = 200;

	Coordinate coordinate = new Coordinate(1.388738, 43.643089);
	Destination home = new Destination("Address", "Home", coordinate, "0");


	public List<Destination> retrieveDestinations(List<Integer> ids, Destination home) {

		List<Destination> destinations = new ArrayList<>();
		destinations.add(home);

		destinations.addAll(ids.stream().map(id -> {
			Doctor doctor = doctorService.getOneDoctor(id);
			Establishment establishment = doctor.getEstablishment();
			Destination destination = new Destination();
			destination.setId("" + id);
			destination.setCoordinate(new Coordinate(establishment.getX(), establishment.getY()));
			destination.setAddress(establishment.getAddress());
			destination.setDoctorName(doctor.getSurname() + " " + doctor.getName());
			destination.setEstablishmentName(establishment.getName());

			return destination;
		}).collect(Collectors.toList()));

		return destinations;
	}

	@Override
	public RoutingDto getVrptw(List<Integer> ids) throws IOException, InterruptedException {

//		 --------------- Define Coordinate of user ------------------------------------

		final LocalTime waitingTime = LocalTime.parse(WAITING_TIME);
		final LocalTime earliestStart = LocalTime.parse(EARLIEST_START);
		final LocalTime latestArrival = LocalTime.parse(LATEST_ARRIVAL);

//		------------------ Retrieve distance and duration from Project OSRM -----------

		List<Destination> destinations = retrieveDestinations(ids, home);
		String json = callHttp(destinations);

		double[][] distances = parseJsonToArrayOfArray(json, "distances");
		double[][] durations = parseJsonToArrayOfArray(json, "durations");

//		-----------------------------------------------------------------------------

		VehicleRoutingProblem.Builder problemBuilder = createProblem(destinations, waitingTime);
		VehicleImpl vehicle = defineVehicle(home, earliestStart, latestArrival, MAX_DESTINATIONS_PER_DAY);
		VehicleRoutingProblem problem = buildProblem(destinations, distances, durations, vehicle, problemBuilder);
		VehicleRoutingProblemSolution bestSolution = findBestSolution(problem);
		printSolution(problem, bestSolution);

		return bestSolutionToRoutingDto(bestSolution);

	}

	@Override
	public RoutingDto getVrptwAll() throws IOException, InterruptedException {
		List<Doctor> doctors = doctorRepository.findAll();

		List<Integer> ids = doctors.stream()
				.map(Doctor::getId)
				.filter(id -> { return doctorService.isProspect(id) || doctorService.isVisitPlannedBeforeNow(id) || doctorService.isVisitPlannedForThisMonth(id); })
				.collect(Collectors.toList());

		return getVrptw(ids);
	}

	public String getUrl(List<Destination> destinations) {
		StringBuilder stringBuilder = new StringBuilder("http://router.project-osrm.org/table/v1/driving/");
		for (Destination destination : destinations) {
			stringBuilder.append(destination.getCoordinate().getX()).append(",")
					.append(destination.getCoordinate().getY()).append(";");
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		stringBuilder.append("?annotations=duration,distance");
		String url = stringBuilder.toString();
		System.out.println(url);
		return url;
	}

	public double[][] parseJsonToArrayOfArray(String json, String field) {

		JsonObject object = new JsonParser().parse(json).getAsJsonObject();
		JsonArray array = object.get(field).getAsJsonArray();

		double[][] output = new double[array.size()][array.size()];

		for (int i = 0; i < array.size(); i++) {
			JsonArray nestedArray = array.get(i).getAsJsonArray();
			for (int j = 0; j < nestedArray.size(); j++) {
				output[i][j] = nestedArray.get(j).getAsFloat();
			}
		}

		return output;
	}

	private String callHttp(List<Destination> destinations) throws IOException, InterruptedException {

		String url = getUrl(destinations);
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
		HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
		return response.body();
	}

	public VehicleRoutingTransportCostsMatrix createMatrix(List<Destination> destinations, double[][] distances, double[][] durations) {
		VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder
				.newInstance(true);

		List<String> ids = destinations.stream().map(Destination::getId).collect(Collectors.toList());
		List<String> names = destinations.stream().map(destination -> destination.getDoctorName())
				.collect(Collectors.toList());

		for (int i = 0; i < ids.size(); i++) {
			for (int j = 0; j < ids.size(); j++) {
				costMatrixBuilder.addTransportDistance(ids.get(i), ids.get(j), distances[j][i]);
				System.out.println("Distance: " + names.get(i) + " to " + names.get(j) + " is " + distances[j][i]);

				costMatrixBuilder.addTransportTime(ids.get(i), ids.get(j), durations[j][i]);
				System.out.println("Time: " + names.get(i) + " to " + names.get(j) + " is " + durations[j][i]);
			}
		}

		return costMatrixBuilder.build();
	}

	public void printSolution(VehicleRoutingProblem problem, VehicleRoutingProblemSolution bestSolution) {
		SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);
	}

	public VehicleRoutingProblem.Builder createProblem(List<Destination> destinations, LocalTime WAITING_TIME) {
		VehicleRoutingProblem.Builder problemBuilder = VehicleRoutingProblem.Builder.newInstance()
				.setFleetSize(FleetSize.FINITE);
		for (Destination destination : destinations.subList(1, destinations.size())) {
			Location location = Location.Builder.newInstance().setCoordinate(destination.getCoordinate())
					.setId(destination.getId()).build();
			// addSizeDimension ??
			Service service = Service.Builder.newInstance(destination.getId()).addSizeDimension(0, 10)
					.setServiceTime(WAITING_TIME.toSecondOfDay()).setLocation(location).setUserData(destination)
					.build();
			problemBuilder.addJob(service);

		}
		return problemBuilder;
	}

	public VehicleImpl defineVehicle(Destination home, LocalTime earliestStart, LocalTime latestArrival,
			int MAX_DESTINATONS_PER_DAY) {
		VehicleType type = VehicleTypeImpl.Builder.newInstance("type").addCapacityDimension(0, MAX_DESTINATONS_PER_DAY)
				.setCostPerDistance(1).build();

		// Define starting (and ending) place
		Location maison = Location.Builder.newInstance().setCoordinate(home.getCoordinate()).setId(home.getId())
				.build();
		// TO DO: define setEarliestStart and setLatestArrival
		return VehicleImpl.Builder.newInstance("vehicle").setStartLocation(maison).setType(type)
				.setEarliestStart(earliestStart.toSecondOfDay()).setLatestArrival(latestArrival.toSecondOfDay())
				.build();
	}

	public VehicleRoutingProblem buildProblem(List<Destination> destinations, double[][] distances,
			double[][] durations, VehicleImpl vehicle, VehicleRoutingProblem.Builder problemBuilder) {
		VehicleRoutingTransportCosts costMatrix = createMatrix(destinations, distances, durations);
		problemBuilder.setRoutingCost(costMatrix).addVehicle(vehicle);
		return problemBuilder.build();
	}

	public VehicleRoutingProblemSolution findBestSolution(VehicleRoutingProblem problem) {
		VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(problem).buildAlgorithm();
		Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
		return Solutions.bestOf(solutions);
	}

	public RoutingDto bestSolutionToRoutingDto(VehicleRoutingProblemSolution bestSolution) {
		RoutingDto routingDto = new RoutingDto();
		List<Destination> destinations = new ArrayList<>();
		
		VehicleRoute bestRoute = bestSolution.getRoutes().stream().findFirst().orElseThrow();
		TourActivities tour = bestRoute.getTourActivities();

		List<TourActivity> activities = tour.getActivities();
		Collection<Job> jobs = tour.getJobs();
		Service[] services = jobs.toArray(new Service[bestSolution.getRoutes().toArray(new VehicleRoute[bestSolution.getRoutes().size()])[0].getTourActivities().getJobs().size()]);
		TourActivity[] tourActivities = activities.toArray(new TourActivity[bestSolution.getRoutes().toArray(new VehicleRoute[bestSolution.getRoutes().size()])[0].getTourActivities().getActivities().size()]);


		for (int i = 0; i < services.length; i++) {
			Destination destination = (Destination) services[i].getUserData();
			TourActivity tourActivity = tourActivities[i];
			destination.setArrivalTime(LocalTime.ofSecondOfDay((long) tourActivity.getArrTime()));
			destination.setEndTime(LocalTime.ofSecondOfDay((long) tourActivity.getEndTime()));
			destination.setIndex(i + 1);

			destinations.add(destination);
		}

		List<Destination> destinationsNotVisited = bestSolution.getUnassignedJobs()
				.stream()
				.map(job -> (Service) job)
				.map(service -> (Destination) service.getUserData())
				.collect(Collectors.toList());


		routingDto.setDestinations(destinations);
		routingDto.setDestinationsNotVisited(destinationsNotVisited);

		return routingDto;

	}
}