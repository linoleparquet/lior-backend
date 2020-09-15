package fr.lino.layani.lior.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.lino.layani.lior.dto.DoctorDto;
import fr.lino.layani.lior.exception.DoctorNotFoundException;
import fr.lino.layani.lior.model.Doctor;
import fr.lino.layani.lior.model.Establishment;
import fr.lino.layani.lior.model.Visit;
import fr.lino.layani.lior.repository.DoctorRepository;

@Service
public class DoctorServiceImpl implements DoctorService {

	@Autowired
	DoctorRepository doctorRepository;

	@Autowired
	VisitService visitService;

	@Autowired
	EstablishmentService establishmentService;

	@Override
	public List<DoctorDto> getAllDoctor() {
		return doctorRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public DoctorDto postCreateOneDoctor(DoctorDto doctorDto) {
		Doctor doctor = toEntity(doctorDto);
		doctorRepository.save(doctor);
		return toDto(doctor);
	}

	@Override
	public DoctorDto getOneDoctor(int id) {
		return doctorRepository.findById(id).map(this::toDto).orElseThrow(() -> new DoctorNotFoundException(id));
	}

	@Override
	public void putUpdateOneDoctor(DoctorDto doctorDto) {
		Doctor doctor = toEntity(doctorDto);
		doctorRepository.save(doctor);
	}

	@Override
	public void deleteOneDoctor(int id) {
		doctorRepository.deleteById(id);
	}

	@Override
	public void updateNextVisit(DoctorDto doctorDto) {

		Doctor doctor = toEntity(doctorDto);

		if (doctor.getVisits() != null) {
			Visit lastVisit = doctor.getVisits().stream().sorted().findFirst().orElseThrow();
			LocalDate nextVisit = lastVisit.getDate().plusMonths(doctor.getPeriodicity());
			doctor.setNextVisit(nextVisit);
		}
	}

	@Override
	public DoctorDto toDto(Doctor doctor) {
		DoctorDto doctorDto = new DoctorDto();

		doctorDto.setEstablishmentId(doctor.getEstablishment().getId());
		doctorDto.setEstablishmentName(doctor.getEstablishment().getName());
		doctorDto.setDepartment(doctor.getEstablishment().getDepartment());
		doctorDto.setId(doctor.getId());
		doctorDto.setName(doctor.getName());
		doctorDto.setNextVisit(doctor.getNextVisit());
		doctorDto.setPeriodicity(doctor.getPeriodicity());
		doctorDto.setSurname(doctor.getSurname());
		doctorDto.setNextVisit(doctor.getNextVisit());
		if (doctor.getVisits() != null) {
			doctor.getVisits().stream().sorted().findFirst()
					.ifPresent(visit -> doctorDto.setLastVisit(visit.getDate()));
		}

		doctorDto.setVisitsDto(
				doctor.getVisits().stream().map(visit -> visitService.toDto(visit)).collect(Collectors.toList()));
		return doctorDto;
	}

	@Override
	public Doctor toEntity(DoctorDto doctorDto) {
		Doctor doctor = new Doctor();
		Establishment establishment = establishmentService
				.toEntity(establishmentService.getOneEstablishment(doctorDto.getEstablishmentId()));

		doctor.setEstablishment(establishment);
		doctor.setId(doctorDto.getId());
		doctor.setName(doctorDto.getName());
		doctor.setNextVisit(doctorDto.getNextVisit());
		doctor.setPeriodicity(doctorDto.getPeriodicity());
		doctor.setSurname(doctorDto.getSurname());

		return doctor;
	}
}
