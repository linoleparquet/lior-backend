package fr.lino.layani.lior.dto;

import java.time.LocalDate;
import java.util.Collection;

import fr.lino.layani.lior.model.Establishment;
import fr.lino.layani.lior.model.Visit;
import lombok.Data;

@Data
public class DoctorDto {
	private int id;
	private String name;
	private String surname;
	private int periodicity;
	private Collection<Visit> visits;
	private LocalDate nextVisit;
	private Establishment establishment;
}
