package fr.lino.layani.lior.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.lino.layani.lior.dto.EstablishmentDto;
import fr.lino.layani.lior.exception.EstablishmentNotFoundException;
import fr.lino.layani.lior.model.Establishment;
import fr.lino.layani.lior.repository.EstablishmentRepository;

@Service
public class EstablishmentServiceImpl implements EstablishmentService {

	@Autowired
	EstablishmentRepository establishmentRepository;

	@Override
	public List<EstablishmentDto> getAllEstablishment() {
		return establishmentRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
	}

	@Override
	public EstablishmentDto getOneEstablishment(int id) {
		return establishmentRepository.findById(id).map(this::toDto)
				.orElseThrow(() -> new EstablishmentNotFoundException(id));
	}

	@Override
	public EstablishmentDto postCreateOneEstablishment(EstablishmentDto establishmentDto) {
		Establishment establishment = toEntity(establishmentDto);
		Establishment establishmentCreated = establishmentRepository.save(establishment);
		return toDto(establishmentCreated);
	}

	@Override
	public void putUpdateOneEstablishment(EstablishmentDto establishmentDto) {
		Establishment establishment = toEntity(establishmentDto);
		establishmentRepository.save(establishment);
	}

	@Override
	public void deleteOneEstablishment(int id) {
		establishmentRepository.deleteById(id);
	}

	@Override
	public EstablishmentDto toDto(Establishment establishment) {
		EstablishmentDto establishmentDto = new EstablishmentDto();

		establishmentDto.setAddress(establishment.getAddress());
		establishmentDto.setCity(establishment.getCity());
		establishmentDto.setDepartment(establishment.getDepartment());
		establishmentDto.setId(establishment.getId());
		establishmentDto.setName(establishment.getName());

		return establishmentDto;
	}

	@Override
	public Establishment toEntity(EstablishmentDto establishmentDto) {
		Establishment establishment = new Establishment();

		establishment.setAddress(establishmentDto.getAddress());
		establishment.setCity(establishmentDto.getCity());
		establishment.setDepartment(establishmentDto.getDepartment());
		establishment.setId(establishmentDto.getId());
		establishment.setName(establishmentDto.getName());
		establishment.setX(establishmentDto.getX());
		establishment.setY(establishmentDto.getY());

		return establishment;
	}
}
