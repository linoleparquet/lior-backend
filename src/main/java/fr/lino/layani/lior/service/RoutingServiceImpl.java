package fr.lino.layani.lior.service;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.graphhopper.jsprit.core.problem.solution.route.activity.*;
import fr.lino.layani.lior.model.*;
import fr.lino.layani.lior.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.util.Coordinate;

import fr.lino.layani.lior.dto.RoutingDto;

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

	@Autowired
	UserPreferenceService userPreferenceService;

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

		UserPreference userPreference = userPreferenceService.getDefaultUserPreference();

		final LocalTime earliestStart = userPreference.getEarliestStart();
		final LocalTime latestArrival = userPreference.getLatestArrival();
		final LocalTime waitingTime = userPreference.getWaitingTime();
		final int maxDestinationPerDay = userPreference.getMaxDestinationPerDay();

		final UserLocation userLocation = userPreference.getUserLocation();
		Coordinate coordinate = new Coordinate(userLocation.getX(), userLocation.getY());
		Destination startingDestination = new Destination(userLocation.getName(), "startingDestination", coordinate, "0");

//		 --------------- Define Coordinate of user ------------------------------------

		// The 'destinations' variable helps creating the distance matrix and the duration matrix.
		List<Destination> destinations = retrieveDestinations(ids, startingDestination);
		// Retrieve distance and duration matrix from OSRM Project
		DistanceDurationMatrices distanceDurationMatrices = osrmProjectService.getDistanceDurationMatrices(destinations);
		// Adding the startingDestination as the first step and the last step of our route
		VehicleRoutingProblemSolution bestSolution = vrptwService.getVehicleRoutingProblemSolution(destinations, waitingTime, startingDestination, earliestStart, latestArrival, maxDestinationPerDay, distanceDurationMatrices);

		String encodedPolyline = getEncodedPolyline(bestSolution, startingDestination);
		//String encodedPolyline = "toto";

		return generatingRoutingDto(bestSolution, startingDestination, encodedPolyline);

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

	private RoutingDto generatingRoutingDto(VehicleRoutingProblemSolution bestSolution, Destination startingDestination, String encodedPolyline) throws CloneNotSupportedException {
		RoutingDto routingDto = new RoutingDto();

		routingDto.setStartingDestination(startingDestination);
		routingDto.setDestinations(getDestinationVisited(bestSolution, startingDestination));
		routingDto.setDestinationsNotVisited(getDestinationNotVisited(bestSolution));
		routingDto.setEncodedPolyline(encodedPolyline);

		return routingDto;
	}

	private List<Destination> getDestinationVisited(VehicleRoutingProblemSolution bestSolution, Destination startingDestination) throws CloneNotSupportedException {
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

	private String getEncodedPolyline(VehicleRoutingProblemSolution bestSolution, Destination startingDestination) throws IOException, InterruptedException, CloneNotSupportedException {
		ArrayList<Destination> destinations = (ArrayList<Destination>) getDestinationVisited(bestSolution, startingDestination);
		return osrmProjectService.getEncodedPolyline(destinations);
	}
}