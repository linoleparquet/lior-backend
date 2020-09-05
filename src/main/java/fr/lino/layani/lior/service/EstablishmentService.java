package fr.lino.layani.lior.service;

import java.util.List;

import fr.lino.layani.lior.model.Establishment;

public interface EstablishmentService {
	List<Establishment> getAllEstablishment();

	Establishment postCreateNewEstablishment(Establishment establishment);

	Establishment getOneEstablishment(int id);

	Establishment putUpdateOneEstablishment(Establishment establishment, int id);

	void deleteOneEstablishment(int id);

}
