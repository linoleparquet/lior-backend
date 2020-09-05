package fr.lino.layani.lior.service;

import java.util.List;

import fr.lino.layani.lior.model.Visit;

public interface VisitService {
	List<Visit> getAllVisit();

	Visit postCreateNewVisit(Visit visit);

	Visit getOneVisit(int id);

	Visit putUpdateOneVisit(Visit visit, int id);

	void deleteOneVisit(int id);

}
