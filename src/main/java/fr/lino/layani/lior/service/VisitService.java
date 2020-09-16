package fr.lino.layani.lior.service;

import java.util.List;

import fr.lino.layani.lior.dto.VisitDto;
import fr.lino.layani.lior.model.Visit;

public interface VisitService {
	List<VisitDto> getAllVisit();

	VisitDto postCreateOneVisit(VisitDto visit);

	VisitDto getOneVisit(int id);

	void putUpdateOneVisit(VisitDto visit);

	void deleteOneVisit(int id);

	VisitDto toDto(Visit visit);

	Visit toEntity(VisitDto visitDto);

	List<VisitDto> findByDoctorId(int id);

}
