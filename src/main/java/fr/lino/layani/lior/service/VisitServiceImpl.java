package fr.lino.layani.lior.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.lino.layani.lior.dto.VisitDto;
import fr.lino.layani.lior.exception.VisitNotFoundException;
import fr.lino.layani.lior.model.Visit;
import fr.lino.layani.lior.repository.VisitRepository;

@Service
public class VisitServiceImpl implements VisitService {

	@Autowired
	VisitRepository visitRepository;

	@Autowired
	DoctorService doctorService;

	@Override
	public List<VisitDto> getAllVisit() {
		return visitRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public VisitDto getOneVisit(int id) {
		return visitRepository.findById(id).map(this::toDto).orElseThrow(() -> new VisitNotFoundException(id));
	}

	@Override
	public VisitDto postCreateOneVisit(VisitDto visitDto) {
		Visit visit = toEntity(visitDto);
		Visit visitCreated = visitRepository.save(visit);
		return toDto(visitCreated);

	}

	@Override
	public void putUpdateOneVisit(VisitDto visitDto) {
		Visit visit = toEntity(visitDto);
		visitRepository.save(visit);
	}

	@Override
	public void deleteOneVisit(int id) {
		visitRepository.deleteById(id);
	}

	@Override
	public VisitDto toDto(Visit visit) {
		VisitDto visitDto = new VisitDto();
		visitDto.setId(visit.getId());
		visitDto.setDate(visit.getDate());
		visitDto.setDoctor(visit.getDoctor());
		visitDto.setNotes(visit.getNotes());
		return visitDto;
	}

	@Override
	public Visit toEntity(VisitDto visitDto) {
		Visit visit = new Visit();
		visit.setId(visitDto.getId());
		visit.setDate(visitDto.getDate());
		visit.setDoctor(visitDto.getDoctor());
		visit.setNotes(visitDto.getNotes());
		return visit;
	}

}
