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

import fr.lino.layani.lior.dto.DoctorDto;
import fr.lino.layani.lior.service.DoctorService;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

	Logger LOGGER = Logger.getLogger(this.getClass().getName());

	@Autowired
	private DoctorService doctorService;

	@GetMapping("/all")
	public List<DoctorDto> getAllDoctor() {
		return doctorService.getAllDoctor();
	}

	@GetMapping
	public List<DoctorDto> findByEstablishmentId(@RequestParam int establishment) {
		return doctorService.findByEstablishmentId(establishment);
	}

	@GetMapping("/{id}")
	public DoctorDto getOneDoctor(@PathVariable int id) {
		return doctorService.toDto(doctorService.getOneDoctor(id));
	}

	@PostMapping
	public DoctorDto postCreateNewDoctor(@RequestBody DoctorDto doctorDto) {
		LOGGER.info("Creating Doctor: " + doctorDto);
		return doctorService.postCreateOneDoctor(doctorDto);
	}

	@PutMapping("/{id}")
	public void putUpdateOneDoctor(@RequestBody DoctorDto doctorDto, @PathVariable int id) {
		LOGGER.info("Updating Doctor: " + doctorDto);
		doctorService.putUpdateOneDoctor(doctorDto);
	}

	@DeleteMapping("/{id}")
	public void deleteOneDoctor(@PathVariable int id) {
		LOGGER.info("Deleting Doctor with id " + id);
		doctorService.deleteOneDoctor(id);
	}
}
