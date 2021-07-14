package fr.lino.layani.lior.service;

import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;
import fr.lino.layani.lior.model.Destination;
import fr.lino.layani.lior.model.DistanceDurationMatrices;

import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class VRPTWServiceImpl implements VRPTWService {

    Logger LOGGER = Logger.getLogger(this.getClass().getName());

    @Override
    public VehicleRoutingProblemSolution getVehicleRoutingProblemSolution(
            List<Destination> destinations,
            LocalTime waitingTime,
            Destination startingDestination,
            LocalTime earliestStart,
            LocalTime latestArrival,
            int MAX_DESTINATIONS_PER_DAY,
            DistanceDurationMatrices distanceDurationMatrices) {

        VehicleRoutingProblem.Builder problemBuilder = createProblem(destinations, waitingTime);
        VehicleImpl vehicle = defineVehicle(startingDestination, earliestStart, latestArrival, MAX_DESTINATIONS_PER_DAY);
        VehicleRoutingProblem problem = buildProblem(destinations, distanceDurationMatrices, vehicle, problemBuilder);
        VehicleRoutingProblemSolution bestSolution = findBestSolution(problem);
        printSolution(problem, bestSolution);

        return bestSolution;
    }

    /**
     * Create the VPRTW Problem.
     * @param destinations destinations to reach
     * @param WAITING_TIME time to spend in each doctor's place
     * @return the VPRTW Problem
     */
    private VehicleRoutingProblem.Builder createProblem(List<Destination> destinations, LocalTime WAITING_TIME) {
        VehicleRoutingProblem.Builder problemBuilder = VehicleRoutingProblem.Builder.newInstance()
                .setFleetSize(VehicleRoutingProblem.FleetSize.FINITE);

        // The first destination of the list is the startingDestination. We don't want to create a Job for the startingDestination
        for (Destination destination : destinations.subList(1, destinations.size())) {

            Location location = Location.Builder.newInstance()
                    .setCoordinate(destination.getCoordinate())
                    .setId(destination.getId()).build();
            Service service = Service.Builder.newInstance(destination.getId())
                    // what is addSizeDimension ??
                    .addSizeDimension(0, 10)
                    .setServiceTime(WAITING_TIME.toSecondOfDay())
                    .setLocation(location)
                    .setUserData(destination)
                    .build();

            problemBuilder.addJob(service);

        }
        return problemBuilder;
    }

    private VehicleImpl defineVehicle(Destination startingDestination, LocalTime earliestStart, LocalTime latestArrival, int MAX_DESTINATIONS_PER_DAY) {
        VehicleType type = VehicleTypeImpl.Builder.newInstance("type")
                .addCapacityDimension(0, MAX_DESTINATIONS_PER_DAY)
                .setCostPerDistance(1)
                .build();

        // Define starting (and ending) place
        Location startLocation = Location.Builder.newInstance()
                .setCoordinate(startingDestination.getCoordinate())
                .setId(startingDestination.getId())
                .build();
        return VehicleImpl.Builder.newInstance("vehicle")
                .setStartLocation(startLocation)
                .setEndLocation(startLocation)
                .setType(type)
                .setEarliestStart(earliestStart.toSecondOfDay())
                .setLatestArrival(latestArrival.toSecondOfDay())
                .build();
    }

    private VehicleRoutingProblem buildProblem(List<Destination> destinations, DistanceDurationMatrices distanceDurationMatrices, VehicleImpl vehicle, VehicleRoutingProblem.Builder problemBuilder) {
        VehicleRoutingTransportCosts costMatrix = createCostMatrix(destinations, distanceDurationMatrices);
        problemBuilder.setRoutingCost(costMatrix).addVehicle(vehicle);
        return problemBuilder.build();
    }

    /**
     * Create the Cost Matrix. The Cost Matrix will decide the order in which the locations will be visited,
     * as well as if they will be visited or not.
     * @param destinations list of destinations needed to create the cost matrix.
     *                     This represent the starting location, and all steps on the road.
     * @param distanceDurationMatrices Object containing the matrix of distances, and the matrix of duration
     * @return Cost Matrix
     */
    private VehicleRoutingTransportCostsMatrix createCostMatrix(List<Destination> destinations, DistanceDurationMatrices distanceDurationMatrices) {
        VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder.newInstance(true);

        double[][] distances = distanceDurationMatrices.getDistanceMatrix();
        double[][] durations = distanceDurationMatrices.getDurationMatrix();

        List<String> ids = destinations.stream().map(Destination::getId).collect(Collectors.toList());
        List<String> names = destinations.stream().map(Destination::getDoctorName).collect(Collectors.toList());

        for (int i = 0; i < ids.size(); i++) {
            for (int j = 0; j < ids.size(); j++) {
                costMatrixBuilder.addTransportDistance(ids.get(i), ids.get(j), distances[j][i]);
                LOGGER.info("Distance: " + names.get(i) + " to " + names.get(j) + " is " + distances[j][i]);

                costMatrixBuilder.addTransportTime(ids.get(i), ids.get(j), durations[j][i]);
                LOGGER.info("Time: " + names.get(i) + " to " + names.get(j) + " is " + durations[j][i]);
            }
        }

        return costMatrixBuilder.build();
    }

    private VehicleRoutingProblemSolution findBestSolution(VehicleRoutingProblem problem) {
        VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(problem).buildAlgorithm();
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
        return Solutions.bestOf(solutions);
    }

    private void printSolution(VehicleRoutingProblem problem, VehicleRoutingProblemSolution bestSolution) {
        SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);
    }
}
