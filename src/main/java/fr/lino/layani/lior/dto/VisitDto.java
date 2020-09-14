package fr.lino.layani.lior.dto;

import java.time.LocalDate;

import fr.lino.layani.lior.model.Doctor;
import lombok.Data;

@Data
public class VisitDto {
	private int id;
	private Doctor doctor;
	private LocalDate date;
	private String notes;
}
