package fr.lino.layani.lior.controller;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.lino.layani.lior.dto.VisitDto;
import fr.lino.layani.lior.service.VisitService;

@RestController
@RequestMapping("/visits")
public class VisitController {

	Logger LOGGER = Logger.getLogger(this.getClass().getName());

	@Autowired
	private VisitService visitService;

	@GetMapping("/all")
	public List<VisitDto> getAllVisit() {
		return visitService.getAllVisit();
	}

	@GetMapping("/{id}")
	public VisitDto getOneVisit(@PathVariable int id) {
		return visitService.getOneVisit(id);
	}

	@GetMapping
	public List<VisitDto> findByDoctorId(@RequestParam int doctor) {
		return visitService.findByDoctorId(doctor);
	}

	@PostMapping
	public VisitDto postCreateOneVisit(@RequestBody VisitDto visitDto) {
		LOGGER.info("Creating Visit :" + visitDto);
		return visitService.postCreateOneVisit(visitDto);
	}

	@PutMapping("/{id}")
	public void putUpdateOneVisit(@RequestBody VisitDto visitDto) {
		LOGGER.info("Updating Visit :" + visitDto);
		visitService.putUpdateOneVisit(visitDto);
	}

	@DeleteMapping("/{id}")
	public void deleteOneVisit(@PathVariable int id) {
		LOGGER.info("Deleting Visit with id:" + id);
		visitService.deleteOneVisit(id);
	}

}
