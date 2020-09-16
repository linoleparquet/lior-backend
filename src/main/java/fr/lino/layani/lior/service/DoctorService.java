package fr.lino.layani.lior.service;

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

}
