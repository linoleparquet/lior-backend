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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.lino.layani.lior.dto.DoctorDto;
import fr.lino.layani.lior.service.DoctorService;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

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
		return doctorService.postCreateOneDoctor(doctorDto);
	}

	@PutMapping("/{id}")
	public void putUpdateOneDoctor(@RequestBody DoctorDto doctorDto, @PathVariable int id) {
		doctorService.putUpdateOneDoctor(doctorDto);
	}

	@DeleteMapping("/{id}")
	public void deleteOneDoctor(@PathVariable int id) {
		doctorService.deleteOneDoctor(id);
	}
}
