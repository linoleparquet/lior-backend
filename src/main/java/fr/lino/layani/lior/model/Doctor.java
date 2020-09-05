package fr.lino.layani.lior.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Doctor {

	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) int id;
	private String name;
	private String surname;
	private int periodicity; // Entier compris entre 1 et 12. tout les x mois /ans
	private Integer lastVisitId;
	private LocalDate nextVisitDate;
	private Establishment establishment;

}