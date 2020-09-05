package fr.lino.layani.lior.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.lino.layani.lior.service.RoutingService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/routing")
public class RoutingController {

	@Autowired
	private RoutingService routingService;

	@GetMapping("/{vehicules}")
	public String getRouting(@PathVariable int vehicules) {
		return routingService.getRouting(vehicules);
	}
}
