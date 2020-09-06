package fr.lino.layani.lior.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.lino.layani.lior.exception.DoctorNotFoundException;
import fr.lino.layani.lior.model.Doctor;
import fr.lino.layani.lior.model.Visit;
import fr.lino.layani.lior.repository.DoctorRepository;

@Service
public class DoctorServiceImpl implements DoctorService {

	@Autowired
	DoctorRepository repo;

	@Autowired
	VisitService visitService;

	@Override
	public List<Doctor> getAllDoctor() {
		repo.findAll().forEach(doctor -> updateNextVisit(doctor));
		return repo.findAll();
	}

	@Override
	public Doctor postCreateNewDoctor(Doctor doctor) {
		updateNextVisit(doctor);
		return repo.save(doctor);
	}

	@Override
	public Doctor getOneDoctor(int id) {
		Doctor doctor = repo.findById(id).orElseThrow(() -> new DoctorNotFoundException(id));
		updateNextVisit(doctor);
		return doctor;
	}

	@Override
	public Doctor putUpdateOneDoctor(Doctor doctor, int id) {
		doctor.setId(id);
		updateNextVisit(doctor);
		return repo.save(doctor);
	}

	@Override
	public void deleteOneDoctor(int id) {
		repo.deleteById(id);
	}

	@Override
	public void updateNextVisit(Doctor doctor) {

		if (doctor.getVisits() != null) {
			Visit lastVisit = doctor.getVisits().stream().sorted().findFirst().orElseThrow();
			LocalDate nextVisit = lastVisit.getDate().plusMonths(doctor.getPeriodicity());
			doctor.setNextVisit(nextVisit);
		}
	}
}
