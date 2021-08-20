package fr.lino.layani.lior.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.lino.layani.lior.model.Destination;
import fr.lino.layani.lior.model.DistanceDurationMatrices;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.logging.Logger;

@Service
public class OSRMProjectServiceImpl implements OSRMProjectService {

    Logger LOGGER = Logger.getLogger(this.getClass().getName());

    @Override
    public String getEncodedPolyline(List<Destination> destinations) throws IOException, InterruptedException {

        String json = requestRouteJson(destinations);

        return parseRouteJson(json);
    }

    @Override
    public DistanceDurationMatrices getDistanceDurationMatrices(List<Destination> destinations) throws IOException, InterruptedException {
        String json = requestTableJson(destinations);

        double[][] distances = parseJsonToArrayOfArray(json, "distances");
        double[][] durations = parseJsonToArrayOfArray(json, "durations");
        DistanceDurationMatrices distanceDurationMatrices = new DistanceDurationMatrices();
        distanceDurationMatrices.setDistanceMatrix(distances);
        distanceDurationMatrices.setDurationMatrix(durations);

        return  distanceDurationMatrices;
    }

    private String requestTableJson(List<Destination> destinations) throws IOException, InterruptedException {

        String url = constructTableUrl(destinations);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 400) {
            return response.body();
        }
        else {
            throw new InterruptedException("OSRM Service is down. Tried to access it through this request: " + url);
        }
    }

    private String requestRouteJson(List<Destination> destinations) throws IOException, InterruptedException {

        String url = constructRouteUrl(destinations);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 400) {
            return response.body();
        }
        else {
            throw new InterruptedException("OSRM Service is down. Tried to access it through this request: " + url);
        }
    }

    private String constructTableUrl(List<Destination> destinations) {
        StringBuilder stringBuilder = new StringBuilder("http://router.project-osrm.org/table/v1/driving/");
        for (Destination destination : destinations) {
            stringBuilder.append(destination.getCoordinate().getX()).append(",")
                    .append(destination.getCoordinate().getY()).append(";");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("?annotations=duration,distance");
        String url = stringBuilder.toString();
        LOGGER.info(url);
        return url;
    }

    private String constructRouteUrl(List<Destination> destinations) {
        StringBuilder stringBuilder = new StringBuilder("http://router.project-osrm.org/route/v1/driving/");
        for (Destination destination : destinations) {
            stringBuilder.append(destination.getCoordinate().getX())
                    .append(",")
                    .append(destination.getCoordinate().getY())
                    .append(";");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        String url = stringBuilder.toString();
        LOGGER.info(url);
        return url;
    }

    private double[][] parseJsonToArrayOfArray(String json, String field) {

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

    private String parseRouteJson(String json) {
        JsonObject object = new JsonParser().parse(json).getAsJsonObject();
        JsonArray routes = object.get("routes").getAsJsonArray();
        JsonObject route = routes.get(0).getAsJsonObject();
        return route.get("geometry").getAsString();
    }
}
