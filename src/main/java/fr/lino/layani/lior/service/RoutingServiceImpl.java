package fr.lino.layani.lior.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;

import fr.lino.layani.lior.model.Doctor;
import fr.lino.layani.lior.model.Establishment;

@org.springframework.stereotype.Service
public class RoutingServiceImpl implements RoutingService {

	@Autowired
	DoctorService doctorService;

	public List<Establishment> retrieveEstablishmentfromDoctorId(List<Integer> ids) {

		return ids.stream().map(id -> {
			Doctor doctor = doctorService.getOneDoctor(id);
			Establishment establishment = doctor.getEstablishment();
			return establishment;
		}).collect(Collectors.toList());

//		// use setUserData()?
//		Establishment e1 = new Establishment();
//		e1.setName("appart");
//		e1.setX(1.443344);
//		e1.setY(43.61149);
//
//		Establishment e2 = new Establishment();
//		e2.setName("travail");
//		e2.setX(1.341036);
//		e2.setY(43.616246);
//
//		Establishment e3 = new Establishment();
//		e3.setName("trawwail");
//		e3.setX(1.4);
//		e3.setY(43.616);
//
//		ArrayList<Establishment> listEstablishment = new ArrayList<>();
//		listEstablishment.add(e1);
//		listEstablishment.add(e2);
//		listEstablishment.add(e3);
//
//		return listEstablishment;

	}

	@Override
	public String getVrptw(List<Integer> ids) throws IOException, InterruptedException {

		// Coordinate of user
		final String MAISON = "maison";
		double x = 1.388738;
		double y = 43.643089;

		List<Establishment> listEstablishment = retrieveEstablishmentfromDoctorId(ids);

//		-----------------------------------------------------------------------------

		String json = callHttp(listEstablishment, x, y);

		double[][] distances = parseJsonToArrayOfArray(json, "distances");
		double[][] durations = parseJsonToArrayOfArray(json, "durations");

//		-----------------------------------------------------------------------------

		VehicleType type = VehicleTypeImpl.Builder.newInstance("type").addCapacityDimension(0, 2).setCostPerDistance(1)
				.build();

		Location maison = Location.Builder.newInstance().setCoordinate(new Coordinate(x, y)).setId(MAISON).build();
		VehicleImpl vehicle = VehicleImpl.Builder.newInstance("vehicle").setStartLocation(maison).setType(type).build();

		VehicleRoutingProblem.Builder problemBuilder = VehicleRoutingProblem.Builder.newInstance()
				.setFleetSize(FleetSize.INFINITE);

		List<String> establishmentName = new ArrayList<>();
		establishmentName.add(MAISON);

		for (Establishment establishment : listEstablishment) {
			establishmentName.add(establishment.getName());
			Coordinate coordinate = new Coordinate(establishment.getX(), establishment.getY());
			Location location = Location.Builder.newInstance().setCoordinate(coordinate).setId(establishment.getName())
					.build();
			Service service = Service.Builder.newInstance(establishment.getName()).addSizeDimension(0, 1)
					.setLocation(location).build();
			problemBuilder.addJob(service);
		}

		VehicleRoutingTransportCosts costMatrix = createMatrix(establishmentName, distances, durations);
		problemBuilder.setRoutingCost(costMatrix).addVehicle(vehicle);
		VehicleRoutingProblem problem = problemBuilder.build();
		VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(problem).buildAlgorithm();
		Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
		VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);
		SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);

		return null;

	}

	public String getUrl(List<Establishment> listEstablishment, double x, double y) {
		StringBuilder stringBuilder = new StringBuilder("http://router.project-osrm.org/table/v1/driving/").append(x)
				.append(",").append(y).append(";");
		for (Establishment establishment : listEstablishment) {
			stringBuilder.append(establishment.getX()).append(",").append(establishment.getY()).append(";");
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		stringBuilder.append("?annotations=duration,distance");
		String url = stringBuilder.toString();
		System.out.println(url);
		return url;
	}

	private String callHttp(List<Establishment> listEstablishment, double x, double y)
			throws IOException, InterruptedException {

		String url = getUrl(listEstablishment, x, y);
		HttpClient httpClient = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
		HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
		return response.body();
	}

	public double[][] parseJsonToArrayOfArray(String json, String field) {

		JsonObject object = new JsonParser().parse(json).getAsJsonObject();
		JsonArray array = object.get(field).getAsJsonArray();

		double[][] output = new double[object.size()][object.size()];

		for (int i = 0; i < array.size(); i++) {
			JsonArray nestedArray = array.get(i).getAsJsonArray();
			for (int j = 0; j < nestedArray.size(); j++) {
				output[i][j] = nestedArray.get(j).getAsFloat();
			}
		}

		return output;
	}

	public VehicleRoutingTransportCostsMatrix createMatrix(List<String> establishmentName, double[][] distances,
			double[][] durations) {
		VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder
				.newInstance(true);

		for (int i = 0; i < establishmentName.size(); i++) {
			for (int j = 0; j < establishmentName.size(); j++) {
				costMatrixBuilder.addTransportDistance(establishmentName.get(i), establishmentName.get(j),
						distances[j][i]);
				System.out.println("Distance: " + establishmentName.get(i) + " to " + establishmentName.get(j) + " is "
						+ distances[j][i]);

				costMatrixBuilder.addTransportTime(establishmentName.get(i), establishmentName.get(j), durations[j][i]);
				System.out.println("Time: " + establishmentName.get(i) + " to " + establishmentName.get(j) + " is "
						+ durations[j][i]);
			}
		}

		return costMatrixBuilder.build();
	}

	@Override
	public String getVrp(int variable) {
		// TODO Auto-generated method stub
		return null;
	}
}