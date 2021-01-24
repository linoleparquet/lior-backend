package fr.lino.layani.lior.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import fr.lino.layani.lior.model.Visit;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.lino.layani.lior.dto.DoctorDto;
import fr.lino.layani.lior.exception.DoctorNotFoundException;
import fr.lino.layani.lior.model.Doctor;
import fr.lino.layani.lior.model.Establishment;
import fr.lino.layani.lior.repository.DoctorRepository;

@Service
public class DoctorServiceImpl implements DoctorService {

	@Autowired
	DoctorRepository doctorRepository;

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
	public Doctor getOneDoctor(int id) {
		return doctorRepository.findById(id).orElseThrow(() -> new DoctorNotFoundException(id));
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
	public DoctorDto toDto(Doctor doctor) {
		DoctorDto doctorDto = new DoctorDto();

		doctorDto.setEstablishmentId(doctor.getEstablishment().getId());
		doctorDto.setEstablishmentName(doctor.getEstablishment().getName());
		doctorDto.setDepartment(doctor.getEstablishment().getDepartment());
		doctorDto.setId(doctor.getId());
		doctorDto.setName(doctor.getName());
		doctorDto.setPeriodicity(doctor.getPeriodicity());
		doctorDto.setSurname(doctor.getSurname());
		if (doctor.getVisits() != null && !doctor.getVisits().isEmpty()) {
			LocalDate lastVisit = findLastVisit(doctor.getId());
			LocalDate nextVisit = lastVisit.plusMonths(doctor.getPeriodicity());
			doctorDto.setLastVisit(lastVisit);
			doctorDto.setNextVisit(nextVisit);
		}

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
		doctor.setPeriodicity(doctorDto.getPeriodicity());
		doctor.setSurname(doctorDto.getSurname());

		return doctor;
	}

	@Override
	public List<DoctorDto> findByEstablishmentId(int establishment) {
		return doctorRepository.findByEstablishmentId(establishment).stream().map(this::toDto)
				.collect(Collectors.toList());
	}

	@Override
	public LocalDate findLastVisit(int id){
		Doctor doctor = getOneDoctor(id);
		return doctor.getVisits().stream().sorted().findFirst().map(Visit::getDate).orElse(null);
	}

	@Override
	public boolean isVisitPlannedBeforeNow(int id){
		Doctor doctor = getOneDoctor(id);
		LocalDate lastVisit = findLastVisit(doctor.getId());
		if(lastVisit != null){
			LocalDate nextVisit = lastVisit.plusMonths(doctor.getPeriodicity());
			return nextVisit.isBefore(LocalDate.now());
		} else {
			return false;
		}
	}

	@Override
	public boolean isVisitPlannedForThisMonth(int id){
		Doctor doctor = getOneDoctor(id);
		LocalDate lastVisit = findLastVisit(doctor.getId());
		LocalDate now = LocalDate.now();
		if(lastVisit != null){
			LocalDate nextVisit = lastVisit.plusMonths(doctor.getPeriodicity());
			return nextVisit.getMonth() == now.getMonth() && nextVisit.getYear() == now.getYear();
		} else {
			return false;
		}
	}

	@Override
	public boolean isProspect(int id){
		Doctor doctor = getOneDoctor(id);
		LocalDate lastVisit = findLastVisit(doctor.getId());
		return lastVisit == null;
	}
}
