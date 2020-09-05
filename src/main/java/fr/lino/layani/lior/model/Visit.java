package fr.lino.layani.lior.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Visit implements Comparable<Visit> {

	private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) int id;
	private int doctorId;
	private LocalDate date;
	@Column(length = 4000)
	private String note;

	@Override
	public int compareTo(Visit o) {
		return o.getDate().compareTo(this.getDate());
	}
}
