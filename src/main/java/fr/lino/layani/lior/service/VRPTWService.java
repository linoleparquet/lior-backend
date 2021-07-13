package fr.lino.layani.lior.service;

import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import fr.lino.layani.lior.model.Destination;
import fr.lino.layani.lior.model.DistanceDurationMatrices;

import java.time.LocalTime;
import java.util.List;

public interface VRPTWService {
    VehicleRoutingProblemSolution getVehicleRoutingProblemSolution(List<Destination> destinations,
                                                                   LocalTime waitingTime,
                                                                   Destination startingDestination,
                                                                   LocalTime earliestStart,
                                                                   LocalTime latestArrival,
                                                                   int MAX_DESTINATIONS_PER_DAY,
                                                                   DistanceDurationMatrices distanceDurationMatrices);
}
