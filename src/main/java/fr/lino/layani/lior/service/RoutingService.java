package fr.lino.layani.lior.service;

import java.io.IOException;
import java.util.List;

import fr.lino.layani.lior.dto.RoutingDto;

public interface RoutingService {

	RoutingDto getVrptw(List<Integer> ids) throws IOException, InterruptedException, CloneNotSupportedException;

    RoutingDto getVrptwAll() throws IOException, InterruptedException, CloneNotSupportedException;
}
