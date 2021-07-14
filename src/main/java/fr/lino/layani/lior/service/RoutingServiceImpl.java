package fr.lino.layani.lior.service;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.graphhopper.jsprit.core.problem.solution.route.activity.*;
import fr.lino.layani.lior.model.DistanceDurationMatrices;
import fr.lino.layani.lior.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
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

	/**
	 * Solve the Vehicle Routing Problem with Time Constraint (VRPTW)
	 * for the doctors whom ids are passed as parameter
	 * @param ids List of doctor's ids that need to be visited
	 * @return RoutingDto Object to be parsed by the frontend
	 * @throws IOException Exception
	 * @throws InterruptedException Exception
	 */
	@Override
	public RoutingDto getVrptw(List<Integer> ids) throws IOException, InterruptedException, CloneNotSupportedException {

//		 --------------- Define Coordinate of user ------------------------------------

		final LocalTime waitingTime = LocalTime.parse(WAITING_TIME);
		final LocalTime earliestStart = LocalTime.parse(EARLIEST_START);
		final LocalTime latestArrival = LocalTime.parse(LATEST_ARRIVAL);

		// The 'destinations' variable helps creating the distance matrix and the duration matrix.
		List<Destination> destinations = retrieveDestinations(ids, startingDestination);
		// Retrieve distance and duration matrix from OSRM Project
		DistanceDurationMatrices distanceDurationMatrices = osrmProjectService.getDistanceDurationMatrices(destinations);
		// Adding the startingDestination as the first step and the last step of our route
		VehicleRoutingProblemSolution bestSolution = vrptwService.getVehicleRoutingProblemSolution(destinations, waitingTime, startingDestination, earliestStart, latestArrival, MAX_DESTINATIONS_PER_DAY, distanceDurationMatrices);

		String encodedPolyline = getEncodedPolyline(bestSolution);
		//String encodedPolyline = "toto";

		return generatingRoutingDto(bestSolution, encodedPolyline);

	}

	/**
	 * Solve the Vehicle Routing Problem with Time Constraint (VRPTW)
	 * for all doctors that needs to be visited.
	 * @return A RoutingDto Object to be parsed by the frontend
	 * @throws IOException Exception
	 * @throws InterruptedException Exception
	 */
	@Override
	public RoutingDto getVrptwAll() throws IOException, InterruptedException, CloneNotSupportedException {
		List<Doctor> doctors = doctorRepository.findAll();

		List<Integer> ids = doctors.stream()
				.map(Doctor::getId)
				.filter(id ->  doctorService.isProspect(id) || doctorService.isVisitPlannedBeforeNow(id) || doctorService.isVisitPlannedForThisMonth(id))
				.collect(Collectors.toList());

		return getVrptw(ids);
	}

	private List<Destination> retrieveDestinations(List<Integer> ids, Destination startingDestination) {

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

	private RoutingDto generatingRoutingDto(VehicleRoutingProblemSolution bestSolution, String encodedPolyline) throws CloneNotSupportedException {
		RoutingDto routingDto = new RoutingDto();

		routingDto.setStartingDestination(startingDestination);
		routingDto.setDestinations(getDestinationVisited(bestSolution));
		routingDto.setDestinationsNotVisited(getDestinationNotVisited(bestSolution));
		routingDto.setEncodedPolyline(encodedPolyline);

		return routingDto;
	}

	private List<Destination> getDestinationVisited(VehicleRoutingProblemSolution bestSolution) throws CloneNotSupportedException {
		List<Destination> destinations = new ArrayList<>();

		VehicleRoute bestRoute = bestSolution.getRoutes().stream().findFirst().orElseThrow();
		TourActivities tour = bestRoute.getTourActivities();
		List<TourActivity> activities = tour.getActivities();

		Start start = bestRoute.getStart();
		Destination startDestination = (Destination) startingDestination.clone();
		startDestination.setDuration(LocalTime.ofSecondOfDay((long)start.getOperationTime()));
		startDestination.setArrivalTime(LocalTime.ofSecondOfDay((long)start.getArrTime()));
		startDestination.setEndTime(LocalTime.ofSecondOfDay((long)start.getEndTime()));
		destinations.add(startDestination);

		for (TourActivity act : activities) {
			PickupService pickupService = (PickupService) act;
			Destination destination = (Destination) pickupService.getJob().getUserData();
			destination.setArrivalTime(LocalTime.ofSecondOfDay((long) pickupService.getArrTime()));
			destination.setEndTime(LocalTime.ofSecondOfDay((long) pickupService.getEndTime()));
			destination.setDuration(LocalTime.ofSecondOfDay((long) pickupService.getOperationTime()));
			destinations.add(destination);
		}
		End end = bestRoute.getEnd();
		Destination endingDestination = (Destination) startingDestination.clone();
		endingDestination.setDuration(LocalTime.ofSecondOfDay((long)end.getOperationTime()));
		endingDestination.setArrivalTime(LocalTime.ofSecondOfDay((long)end.getArrTime()));
		endingDestination.setEndTime(LocalTime.ofSecondOfDay((long)end.getEndTime()));
		destinations.add(endingDestination);

		return destinations;
	}

	private List<Destination> getDestinationNotVisited(VehicleRoutingProblemSolution bestSolution) {
		return bestSolution.getUnassignedJobs()
				.stream()
				.map(job -> (Service) job)
				.map(service -> (Destination) service.getUserData())
				.collect(Collectors.toList());
	}

	private String getEncodedPolyline(VehicleRoutingProblemSolution bestSolution) throws IOException, InterruptedException, CloneNotSupportedException {
		ArrayList<Destination> destinations = (ArrayList<Destination>) getDestinationVisited(bestSolution);
		return osrmProjectService.getEncodedPolyline(destinations);
	}
}