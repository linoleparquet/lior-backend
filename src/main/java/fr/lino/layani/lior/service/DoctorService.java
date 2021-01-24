package fr.lino.layani.lior.service;

import java.time.LocalDate;
import java.util.List;

import fr.lino.layani.lior.dto.DoctorDto;
import fr.lino.layani.lior.model.Doctor;

public interface DoctorService {

	List<DoctorDto> getAllDoctor();

	DoctorDto postCreateOneDoctor(DoctorDto doctorDto);

	Doctor getOneDoctor(int id);

	void putUpdateOneDoctor(DoctorDto doctorDto);

	void deleteOneDoctor(int id);

	DoctorDto toDto(Doctor doctor);

	Doctor toEntity(DoctorDto doctorDto);

	List<DoctorDto> findByEstablishmentId(int establishment);

	LocalDate findLastVisit(int id);

	boolean isVisitPlannedBeforeNow(int id);

	boolean isVisitPlannedForThisMonth(int id);

	boolean isProspect(int id);

}
