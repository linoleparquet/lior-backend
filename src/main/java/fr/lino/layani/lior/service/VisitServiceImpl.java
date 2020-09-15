package fr.lino.layani.lior.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.lino.layani.lior.exception.VisitNotFoundException;
import fr.lino.layani.lior.model.Visit;
import fr.lino.layani.lior.repository.VisitRepository;

@Service
public class VisitServiceImpl implements VisitService {

	@Autowired
	VisitRepository repo;

	@Autowired
	DoctorService doctorService;

	@Override
	public List<Visit> getAllVisit() {
		return repo.findAll();
	}

	@Override
	public Visit getOneVisit(int id) {
		return repo.findById(id).orElseThrow(() -> new VisitNotFoundException(id));
	}

	@Override
	public Visit postCreateNewVisit(Visit visit) {
		return repo.save(visit);
	}

	@Override
	public Visit putUpdateOneVisit(Visit updatedVisit, int id) {
		return repo.save(updatedVisit);
	}

	@Override
	public void deleteOneVisit(int id) {
		repo.deleteById(id);
	}

}
