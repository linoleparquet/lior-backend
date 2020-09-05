package fr.lino.layani.lior.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.lino.layani.lior.model.Establishment;
import fr.lino.layani.lior.service.EstablishmentService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/establishments")
public class EstablishmentController {

	@Autowired
	private EstablishmentService establishmentService;

	@GetMapping
	public List<Establishment> getAllEstablishment() {
		return establishmentService.getAllEstablishment();
	}

	@GetMapping("/{id}")
	public Establishment getOneEstablishment(@PathVariable int id) {
		return establishmentService.getOneEstablishment(id);
	}

	@PostMapping
	public Establishment postCreateNewEstablishment(@RequestBody Establishment establishment) {
		return establishmentService.postCreateNewEstablishment(establishment);
	}

	@PutMapping("/{id}")
	public Establishment putUpdateOneEstablishment(@RequestBody Establishment establishment, @PathVariable int id) {
		return establishmentService.putUpdateOneEstablishment(establishment, id);
	}

	@DeleteMapping("/{id}")
	public void deleteOneEstablishment(@PathVariable int id) {
		establishmentService.deleteOneEstablishment(id);
	}
}
