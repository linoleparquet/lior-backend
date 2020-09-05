package fr.lino.layani.lior.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
		repo.findAll().forEach(doctor -> updateLastVisitNextVisitField(doctor.getId()));
		return repo.findAll();
	}

	@Override
	public Doctor postCreateNewDoctor(Doctor doctor) {
		return repo.save(doctor);
	}

	@Override
	public Doctor getOneDoctor(int id) {
		updateLastVisitNextVisitField(id);
		return repo.findById(id).orElseThrow(() -> new DoctorNotFoundException(id));
	}

	@Override
	public Doctor putUpdateOneDoctor(Doctor doctor, int id) {
		doctor.setId(id);
		return repo.save(doctor);
	}

	@Override
	public void deleteOneDoctor(int id) {
		getVisitByDotor(id).forEach(v -> visitService.deleteOneVisit(v.getId()));
		repo.deleteById(id);
	}

	@Override
	public void updateLastVisitNextVisitField(int id) {
		Doctor doctor = repo.findById(id).orElseThrow(() -> new DoctorNotFoundException(id));

		Visit lastVisit = visitService.getAllVisit().stream().filter(v -> v.getDoctorId() == doctor.getId()).sorted()
				.findFirst().orElse(null);

		if (lastVisit != null) {
			LocalDate nextVisitDate = lastVisit.getDate().plusMonths(doctor.getPeriodicity());
			doctor.setLastVisitId(lastVisit.getId());
			doctor.setNextVisitDate(nextVisitDate);
		}

		putUpdateOneDoctor(doctor, doctor.getId());
	}

	@Override
	public List<Visit> getVisitByDotor(int id) {
		return visitService.getAllVisit().stream().filter(v -> v.getDoctorId() == id).sorted()
				.collect(Collectors.toList());
	}

}
