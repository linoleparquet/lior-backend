package fr.lino.layani.lior.service;

import fr.lino.layani.lior.model.Destination;
import fr.lino.layani.lior.model.DistanceDurationMatrices;

import java.io.IOException;
import java.util.List;

public interface OSRMProjectService {
    DistanceDurationMatrices getDistanceDurationMatrices(List<Destination> destinations) throws IOException, InterruptedException;

    String getEncodedPolyline(List<Destination> destinations) throws IOException, InterruptedException;
}
