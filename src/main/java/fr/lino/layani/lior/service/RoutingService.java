package fr.lino.layani.lior.service;

import java.io.IOException;
import java.util.List;

public interface RoutingService {

	String getVrptw(List<Integer> ids) throws IOException, InterruptedException;

	String getVrp(int variable);

}
