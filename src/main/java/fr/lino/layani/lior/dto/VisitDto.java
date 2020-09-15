package fr.lino.layani.lior.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class VisitDto {
	private int id;
	private int doctorId;
	private String doctorName;
	private LocalDate date;
	private String notes;
}
