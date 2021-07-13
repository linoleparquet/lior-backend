package fr.lino.layani.lior.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.lino.layani.lior.model.Destination;
import fr.lino.layani.lior.model.DistanceDurationMatrices;
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
    public DistanceDurationMatrices getDistanceDurationMatrices(List<Destination> destinations) throws IOException, InterruptedException {
        String json = callHttp(destinations);

        double[][] distances = parseJsonToArrayOfArray(json, "distances");
        double[][] durations = parseJsonToArrayOfArray(json, "durations");
        DistanceDurationMatrices distanceDurationMatrices = new DistanceDurationMatrices();
        distanceDurationMatrices.setDistanceMatrix(distances);
        distanceDurationMatrices.setDurationMatrix(durations);

        return  distanceDurationMatrices;
    }

    private String callHttp(List<Destination> destinations) throws IOException, InterruptedException {

        String url = getUrl(destinations);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private String getUrl(List<Destination> destinations) {
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
}
