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

import fr.lino.layani.lior.model.Doctor;
import fr.lino.layani.lior.service.DoctorService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/doctors")
public class DoctorController {

	@Autowired
	private DoctorService doctorService;

	@GetMapping
	public List<Doctor> getAllDoctor() {
		return doctorService.getAllDoctor();
	}

	@GetMapping("/{id}")
	public Doctor getOneDoctor(@PathVariable int id) {
		return doctorService.getOneDoctor(id);
	}

	@PostMapping
	public Doctor postCreateNewDoctor(@RequestBody Doctor doctor) {
		return doctorService.postCreateNewDoctor(doctor);
	}

	@PutMapping("/{id}")
	public Doctor putUpdateOneDoctor(@RequestBody Doctor doctor, @PathVariable int id) {
		return doctorService.putUpdateOneDoctor(doctor, id);
	}

	@DeleteMapping("/{id}")
	public void deleteOneDoctor(@PathVariable int id) {
		doctorService.deleteOneDoctor(id);
	}
}
