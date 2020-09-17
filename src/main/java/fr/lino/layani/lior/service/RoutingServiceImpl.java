package fr.lino.layani.lior.service;

import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.google.ortools.constraintsolver.Assignment;
import com.google.ortools.constraintsolver.FirstSolutionStrategy;
import com.google.ortools.constraintsolver.IntVar;
import com.google.ortools.constraintsolver.RoutingDimension;
import com.google.ortools.constraintsolver.RoutingIndexManager;
import com.google.ortools.constraintsolver.RoutingModel;
import com.google.ortools.constraintsolver.RoutingSearchParameters;
import com.google.ortools.constraintsolver.main;

@Service
public class RoutingServiceImpl implements RoutingService {

	static {
		System.loadLibrary("lib/jniortools");
	}

	private static final Logger logger = Logger.getLogger(RoutingServiceImpl.class.getName());

	static class DataModelVrptw {
		public final long[][] timeMatrix = { { 0, 6, 9, 8, 7, 3, 6, 2, 3, 2, 6, 6, 4, 4, 5, 9, 7 },
				{ 6, 0, 8, 3, 2, 6, 8, 4, 8, 8, 13, 7, 5, 8, 12, 10, 14 },
				{ 9, 8, 0, 11, 10, 6, 3, 9, 5, 8, 4, 15, 14, 13, 9, 18, 9 },
				{ 8, 3, 11, 0, 1, 7, 10, 6, 10, 10, 14, 6, 7, 9, 14, 6, 16 },
				{ 7, 2, 10, 1, 0, 6, 9, 4, 8, 9, 13, 4, 6, 8, 12, 8, 14 },
				{ 3, 6, 6, 7, 6, 0, 2, 3, 2, 2, 7, 9, 7, 7, 6, 12, 8 },
				{ 6, 8, 3, 10, 9, 2, 0, 6, 2, 5, 4, 12, 10, 10, 6, 15, 5 },
				{ 2, 4, 9, 6, 4, 3, 6, 0, 4, 4, 8, 5, 4, 3, 7, 8, 10 },
				{ 3, 8, 5, 10, 8, 2, 2, 4, 0, 3, 4, 9, 8, 7, 3, 13, 6 },
				{ 2, 8, 8, 10, 9, 2, 5, 4, 3, 0, 4, 6, 5, 4, 3, 9, 5 },
				{ 6, 13, 4, 14, 13, 7, 4, 8, 4, 4, 0, 10, 9, 8, 4, 13, 4 },
				{ 6, 7, 15, 6, 4, 9, 12, 5, 9, 6, 10, 0, 1, 3, 7, 3, 10 },
				{ 4, 5, 14, 7, 6, 7, 10, 4, 8, 5, 9, 1, 0, 2, 6, 4, 8 },
				{ 4, 8, 13, 9, 8, 7, 10, 3, 7, 4, 8, 3, 2, 0, 4, 5, 6 },
				{ 5, 12, 9, 14, 12, 6, 6, 7, 3, 3, 4, 7, 6, 4, 0, 9, 2 },
				{ 9, 10, 18, 6, 8, 12, 15, 8, 13, 9, 13, 3, 4, 5, 9, 0, 9 },
				{ 7, 14, 9, 16, 14, 8, 5, 10, 6, 5, 4, 10, 8, 6, 2, 9, 0 }, };
		public final long[][] timeWindows = { { 0, 5 }, // depot
				{ 7, 12 }, // 1
				{ 10, 15 }, // 2
				{ 16, 18 }, // 3
				{ 10, 13 }, // 4
				{ 0, 5 }, // 5
				{ 5, 10 }, // 6
				{ 0, 4 }, // 7
				{ 5, 10 }, // 8
				{ 0, 3 }, // 9
				{ 10, 16 }, // 10
				{ 10, 15 }, // 11
				{ 0, 5 }, // 12
				{ 5, 10 }, // 13
				{ 7, 8 }, // 14
				{ 10, 15 }, // 15
				{ 11, 15 }, // 16
		};
		public int vehicleNumber = 4;
		public final int depot = 0;
	}

	/// @brief Print the solution.
	static void printSolutionVrptw(DataModelVrptw data, RoutingModel routing, RoutingIndexManager manager,
			Assignment solution) {
		RoutingDimension timeDimension = routing.getMutableDimension("Time");
		long totalTime = 0;
		for (int i = 0; i < data.vehicleNumber; ++i) {
			long index = routing.start(i);
			logger.info("Route for Vehicle " + i + ":");
			String route = "";
			while (!routing.isEnd(index)) {
				IntVar timeVar = timeDimension.cumulVar(index);
				route += manager.indexToNode(index) + " Time(" + solution.min(timeVar) + "," + solution.max(timeVar)
						+ ") -> ";
				index = solution.value(routing.nextVar(index));
			}
			IntVar timeVar = timeDimension.cumulVar(index);
			route += manager.indexToNode(index) + " Time(" + solution.min(timeVar) + "," + solution.max(timeVar) + ")";
			logger.info(route);
			logger.info("Time of the route: " + solution.min(timeVar) + "min");
			totalTime += solution.min(timeVar);
		}
		logger.info("Total time of all routes: " + totalTime + "min");
	}

	@Override
	public String getVrptw(int variable) {

		// Instantiate the data problem.
		final DataModelVrptw data = new DataModelVrptw();

		// Create Routing Index Manager
//		RoutingIndexManager manager = new RoutingIndexManager(data.timeMatrix.length, data.vehicleNumber, data.depot);
		RoutingIndexManager manager = new RoutingIndexManager(data.timeMatrix.length, variable, data.depot);
//		RoutingIndexManager manager = new RoutingIndexManager(data.timeMatrix.length, data.vehicleNumber, variable);

		// Create Routing Model.
		RoutingModel routing = new RoutingModel(manager);

		// Create and register a transit callback.
		final int transitCallbackIndex = routing.registerTransitCallback((long fromIndex, long toIndex) -> {
			// Convert from routing variable Index to user NodeIndex.
			int fromNode = manager.indexToNode(fromIndex);
			int toNode = manager.indexToNode(toIndex);
			return data.timeMatrix[fromNode][toNode];
		});

		// Define cost of each arc.
		routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

		// Add Time constraint.
		routing.addDimension(transitCallbackIndex, // transit callback
				30, // allow waiting time
				30, // vehicle maximum capacities
				false, // start cumul to zero
				"Time");
		RoutingDimension timeDimension = routing.getMutableDimension("Time");
		// Add time window constraints for each location except depot.
		for (int i = 1; i < data.timeWindows.length; ++i) {
			long index = manager.nodeToIndex(i);
			timeDimension.cumulVar(index).setRange(data.timeWindows[i][0], data.timeWindows[i][1]);
		}
		// Add time window constraints for each vehicle start node.
		for (int i = 0; i < data.vehicleNumber; ++i) {
			long index = routing.start(i);
			timeDimension.cumulVar(index).setRange(data.timeWindows[0][0], data.timeWindows[0][1]);
		}

		// Instantiate route start and end times to produce feasible times.
		for (int i = 0; i < data.vehicleNumber; ++i) {
			routing.addVariableMinimizedByFinalizer(timeDimension.cumulVar(routing.start(i)));
			routing.addVariableMinimizedByFinalizer(timeDimension.cumulVar(routing.end(i)));
		}

		// Setting first solution heuristic.
		RoutingSearchParameters searchParameters = main.defaultRoutingSearchParameters().toBuilder()
				.setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC).build();

		// Solve the problem.
		Assignment solution = routing.solveWithParameters(searchParameters);

		// Print solution on console.
		printSolutionVrptw(data, routing, manager, solution);

		// Hello World
		return "VRPTW";
	}

	// ---------------------------------------------------------------------------------------------------------

	static class DataModelVrp {
		public final long[][] distanceMatrix = {
				{ 0, 548, 776, 696, 582, 274, 502, 194, 308, 194, 536, 502, 388, 354, 468, 776, 662 },
				{ 548, 0, 684, 308, 194, 502, 730, 354, 696, 742, 1084, 594, 480, 674, 1016, 868, 1210 },
				{ 776, 684, 0, 992, 878, 502, 274, 810, 468, 742, 400, 1278, 1164, 1130, 788, 1552, 754 },
				{ 696, 308, 992, 0, 114, 650, 878, 502, 844, 890, 1232, 514, 628, 822, 1164, 560, 1358 },
				{ 582, 194, 878, 114, 0, 536, 764, 388, 730, 776, 1118, 400, 514, 708, 1050, 674, 1244 },
				{ 274, 502, 502, 650, 536, 0, 228, 308, 194, 240, 582, 776, 662, 628, 514, 1050, 708 },
				{ 502, 730, 274, 878, 764, 228, 0, 536, 194, 468, 354, 1004, 890, 856, 514, 1278, 480 },
				{ 194, 354, 810, 502, 388, 308, 536, 0, 342, 388, 730, 468, 354, 320, 662, 742, 856 },
				{ 308, 696, 468, 844, 730, 194, 194, 342, 0, 274, 388, 810, 696, 662, 320, 1084, 514 },
				{ 194, 742, 742, 890, 776, 240, 468, 388, 274, 0, 342, 536, 422, 388, 274, 810, 468 },
				{ 536, 1084, 400, 1232, 1118, 582, 354, 730, 388, 342, 0, 878, 764, 730, 388, 1152, 354 },
				{ 502, 594, 1278, 514, 400, 776, 1004, 468, 810, 536, 878, 0, 114, 308, 650, 274, 844 },
				{ 388, 480, 1164, 628, 514, 662, 890, 354, 696, 422, 764, 114, 0, 194, 536, 388, 730 },
				{ 354, 674, 1130, 822, 708, 628, 856, 320, 662, 388, 730, 308, 194, 0, 342, 422, 536 },
				{ 468, 1016, 788, 1164, 1050, 514, 514, 662, 320, 274, 388, 650, 536, 342, 0, 764, 194 },
				{ 776, 868, 1552, 560, 674, 1050, 1278, 742, 1084, 810, 1152, 274, 388, 422, 764, 0, 798 },
				{ 662, 1210, 754, 1358, 1244, 708, 480, 856, 514, 468, 354, 844, 730, 536, 194, 798, 0 }, };
		public final int vehicleNumber = 4;
		public final int depot = 0;
	}

	/// @brief Print the solution.
	static void printSolutionVrp(DataModelVrp data, RoutingModel routing, RoutingIndexManager manager,
			Assignment solution) {
		// Inspect solution.
		long maxRouteDistance = 0;
		for (int i = 0; i < data.vehicleNumber; ++i) {
			long index = routing.start(i);
			logger.info("Route for Vehicle " + i + ":");
			long routeDistance = 0;
			String route = "";
			while (!routing.isEnd(index)) {
				route += manager.indexToNode(index) + " -> ";
				long previousIndex = index;
				index = solution.value(routing.nextVar(index));
				routeDistance += routing.getArcCostForVehicle(previousIndex, index, i);
			}
			logger.info(route + manager.indexToNode(index));
			logger.info("Distance of the route: " + routeDistance + "m");
			maxRouteDistance = Math.max(routeDistance, maxRouteDistance);
		}
		logger.info("Maximum of the route distances: " + maxRouteDistance + "m");
	}

	@Override
	public String getVrp(int variable) {
		// Instantiate the data problem.
		final DataModelVrp data = new DataModelVrp();

		// Create Routing Index Manager
//		RoutingIndexManager manager = new RoutingIndexManager(data.distanceMatrix.length, data.vehicleNumber,
//				data.depot);
		RoutingIndexManager manager = new RoutingIndexManager(data.distanceMatrix.length, variable, data.depot);

		// Create Routing Model.
		RoutingModel routing = new RoutingModel(manager);

		// Create and register a transit callback.
		final int transitCallbackIndex = routing.registerTransitCallback((long fromIndex, long toIndex) -> {
			// Convert from routing variable Index to user NodeIndex.
			int fromNode = manager.indexToNode(fromIndex);
			int toNode = manager.indexToNode(toIndex);
			return data.distanceMatrix[fromNode][toNode];
		});

		// Define cost of each arc.
		routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

		// Add Distance constraint.
		routing.addDimension(transitCallbackIndex, 0, 3000, true, // start cumul to zero
				"Distance");
		RoutingDimension distanceDimension = routing.getMutableDimension("Distance");
		distanceDimension.setGlobalSpanCostCoefficient(100);

		// Setting first solution heuristic.
		RoutingSearchParameters searchParameters = main.defaultRoutingSearchParameters().toBuilder()
				.setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC).build();

		// Solve the problem.
		Assignment solution = routing.solveWithParameters(searchParameters);

		// Print solution on console.
		printSolutionVrp(data, routing, manager, solution);

		// Return statement
		String version = System.getProperty("java.version");
		return "VRP" + version;
	}

}