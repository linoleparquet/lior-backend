package fr.lino.layani.lior.service;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivities;
import fr.lino.layani.lior.model.DistanceDurationMatrices;
import fr.lino.layani.lior.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.util.Coordinate;

import fr.lino.layani.lior.dto.RoutingDto;
import fr.lino.layani.lior.model.Destination;
import fr.lino.layani.lior.model.Doctor;
import fr.lino.layani.lior.model.Establishment;

@org.springframework.stereotype.Service
public class RoutingServiceImpl implements RoutingService {

	Logger LOGGER = Logger.getLogger(this.getClass().getName());

	@Autowired
	DoctorService doctorService;

	@Autowired
	OSRMProjectService osrmProjectService;

	@Autowired
	VRPTWService vrptwService;

	@Autowired
	DoctorRepository doctorRepository;

	final String EARLIEST_START = "09:00";
	final String LATEST_ARRIVAL = "18:00";
	final String WAITING_TIME = "00:30";
	final int MAX_DESTINATIONS_PER_DAY = 200;

	Coordinate coordinate = new Coordinate(1.388738, 43.643089);
	Destination startingDestination = new Destination("Address", "startingDestination", coordinate, "0");

	@Override
	public RoutingDto getVrptw(List<Integer> ids) throws IOException, InterruptedException {

//		 --------------- Define Coordinate of user ------------------------------------

		final LocalTime waitingTime = LocalTime.parse(WAITING_TIME);
		final LocalTime earliestStart = LocalTime.parse(EARLIEST_START);
		final LocalTime latestArrival = LocalTime.parse(LATEST_ARRIVAL);

//		------------------ Retrieve distance and duration matrix from OSRM Project -----------

		List<Destination> destinations = retrieveDestinations(ids, startingDestination);
		DistanceDurationMatrices distanceDurationMatrices = osrmProjectService.getDistanceDurationMatrices(destinations);

//		-----------------------------------------------------------------------------

		VehicleRoutingProblemSolution bestSolution = vrptwService.getVehicleRoutingProblemSolution(destinations, waitingTime, startingDestination, earliestStart, latestArrival, MAX_DESTINATIONS_PER_DAY, distanceDurationMatrices);

		String encodedPolyline = getEncodedPolyline(bestSolution);

		return bestSolutionToRoutingDto(bestSolution, encodedPolyline);

	}

	@Override
	public RoutingDto getVrptwAll() throws IOException, InterruptedException {
		List<Doctor> doctors = doctorRepository.findAll();

		List<Integer> ids = doctors.stream()
				.map(Doctor::getId)
				.filter(id ->  doctorService.isProspect(id) || doctorService.isVisitPlannedBeforeNow(id) || doctorService.isVisitPlannedForThisMonth(id))
				.collect(Collectors.toList());

		return getVrptw(ids);
	}

	public List<Destination> retrieveDestinations(List<Integer> ids, Destination startingDestination) {

		List<Destination> destinations = new ArrayList<>();
		// We add the startingDestination to generate the duration & distance matrix
		destinations.add(startingDestination);

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

	private RoutingDto bestSolutionToRoutingDto(VehicleRoutingProblemSolution bestSolution, String encodedPolyline) {
		RoutingDto routingDto = new RoutingDto();

		routingDto.setStartingDestination(startingDestination);
		routingDto.setDestinations(getDestinationVisited(bestSolution));
		routingDto.setDestinationsNotVisited(getDestinationNotVisited(bestSolution));
		routingDto.setEncodedPolyline(encodedPolyline);

		return routingDto;
	}

	private List<Destination> getDestinationVisited(VehicleRoutingProblemSolution bestSolution) {
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
			destination.setDuration(LocalTime.ofSecondOfDay((long) tourActivity.getOperationTime()));
			destination.setIndex(i + 1);
			destinations.add(destination);
		}

		return destinations;
	}

	private List<Destination> getDestinationNotVisited(VehicleRoutingProblemSolution bestSolution) {
		return bestSolution.getUnassignedJobs()
				.stream()
				.map(job -> (Service) job)
				.map(service -> (Destination) service.getUserData())
				.collect(Collectors.toList());
	}

	private String getEncodedPolyline(VehicleRoutingProblemSolution bestSolution) throws IOException, InterruptedException {

		// adding the starting destination at the beginning and the end of the tour.
		ArrayList<Destination> destinations = (ArrayList<Destination>) getDestinationVisited(bestSolution);
		destinations.add(0, startingDestination);
		destinations.add(startingDestination);

		return osrmProjectService.getEncodedPolyline(destinations);
	}
}