package fr.lino.layani.lior.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DoctorDto {
	private int id;
	private int establishmentId;
	private String establishmentName;
	private String department;
	private String name;
	private String surname;
	private int periodicity;
	private LocalDate lastVisit;
	private LocalDate nextVisit;
}
