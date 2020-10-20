package fr.lino.layani.lior.dto;

import java.util.List;

import fr.lino.layani.lior.model.Destination;
import lombok.Data;

@Data
public class RoutingDto {

	private List<Destination> destinations;
	private List<Destination> destinationsNotVisited;
}
