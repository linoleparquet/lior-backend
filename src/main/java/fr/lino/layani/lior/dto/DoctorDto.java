package fr.lino.layani.lior.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class DoctorDto {
	private int id;
	private int establishmentId;
	private String establishmentName;
	private Integer department;
	private String name;
	private String surname;
	private int periodicity;
	private LocalDate lastVisit;
	private LocalDate nextVisit;
	private List<VisitDto> visitsDto;
}
