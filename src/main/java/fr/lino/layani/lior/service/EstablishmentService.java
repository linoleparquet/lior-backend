package fr.lino.layani.lior.service;

import java.util.List;

import fr.lino.layani.lior.dto.EstablishmentDto;
import fr.lino.layani.lior.model.Establishment;

public interface EstablishmentService {

	List<EstablishmentDto> getAllEstablishment();

	EstablishmentDto getOneEstablishment(int id);

	EstablishmentDto postCreateOneEstablishment(EstablishmentDto establishmentDto);

	void putUpdateOneEstablishment(EstablishmentDto establishmentDto);

	void deleteOneEstablishment(int id);

	EstablishmentDto toDto(Establishment establishment);

	Establishment toEntity(EstablishmentDto establishmentDto);

}
