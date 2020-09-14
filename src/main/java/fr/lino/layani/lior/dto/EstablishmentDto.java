package fr.lino.layani.lior.dto;

import java.util.Collection;

import fr.lino.layani.lior.model.Doctor;
import lombok.Data;

@Data
public class EstablishmentDto {
	private int id;
	private String name;
	private Integer department;
	private String city;
	private String address;
	private Collection<Doctor> doctors;
}
