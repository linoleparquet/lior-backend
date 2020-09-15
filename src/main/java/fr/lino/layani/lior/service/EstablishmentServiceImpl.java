package fr.lino.layani.lior.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.lino.layani.lior.exception.EstablishmentNotFoundException;
import fr.lino.layani.lior.model.Establishment;
import fr.lino.layani.lior.repository.EstablishmentRepository;

@Service
public class EstablishmentServiceImpl implements EstablishmentService {

	@Autowired
	EstablishmentRepository repo;

	@Override
	public List<Establishment> getAllEstablishment() {
		return repo.findAll();
	}

	@Override
	public Establishment getOneEstablishment(int id) {
		return repo.findById(id).orElseThrow(() -> new EstablishmentNotFoundException(id));
	}

	@Override
	public Establishment postCreateNewEstablishment(Establishment establishment) {
		return repo.save(establishment);
	}

	@Override
	public Establishment putUpdateOneEstablishment(Establishment updatedEstablishment, int id) {
		return repo.save(updatedEstablishment);
	}

	@Override
	public void deleteOneEstablishment(int id) {
		repo.deleteById(id);
	}

}
