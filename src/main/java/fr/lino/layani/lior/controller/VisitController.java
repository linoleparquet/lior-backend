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

import fr.lino.layani.lior.dto.VisitDto;
import fr.lino.layani.lior.service.VisitService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/visits")
public class VisitController {

	@Autowired
	private VisitService visitService;

	@GetMapping
	public List<VisitDto> getAllVisit() {
		return visitService.getAllVisit();
	}

	@GetMapping("/{id}")
	public VisitDto getOneVisit(@PathVariable int id) {
		return visitService.getOneVisit(id);
	}

	@PostMapping
	public VisitDto postCreateOneVisit(@RequestBody VisitDto visitDto) {
		return visitService.postCreateOneVisit(visitDto);
	}

	@PutMapping("/{id}")
	public void putUpdateOneVisit(@RequestBody VisitDto visitDto) {
		visitService.putUpdateOneVisit(visitDto);
	}

	@DeleteMapping("/{id}")
	public void deleteOneVisit(@PathVariable int id) {
		visitService.deleteOneVisit(id);
	}
}
