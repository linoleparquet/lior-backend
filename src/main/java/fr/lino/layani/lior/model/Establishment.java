package fr.lino.layani.lior.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Establishment {

	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) int id;
	private String name;
	private Integer department;
	private String city;
	private String address;

	public Establishment() {
	}

}