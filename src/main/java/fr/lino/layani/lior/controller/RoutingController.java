package fr.lino.layani.lior.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.lino.layani.lior.dto.RoutingDto;
import fr.lino.layani.lior.service.RoutingService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/routing")
public class RoutingController {

	@Autowired
	private RoutingService routingService;

	@GetMapping("/VRPTW")
	public RoutingDto getVrptw(@RequestParam List<Integer> ids) throws IOException, InterruptedException {
		return routingService.getVrptw(ids);
	}
}
