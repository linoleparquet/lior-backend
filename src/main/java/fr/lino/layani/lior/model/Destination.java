package fr.lino.layani.lior.model;

import java.time.LocalTime;

import com.graphhopper.jsprit.core.util.Coordinate;

import lombok.Data;

@Data
public class Destination {

	String id;
	String establishmentName;
	String doctorName;
	String horaires;
	String address;
	Coordinate coordinate;
	int index;
	LocalTime arrivalTime;
	LocalTime endTime;
	LocalTime duration;

	public Destination(String address, String establishmentName, Coordinate coordinate, String id){
		this.address = address;
		this.establishmentName = establishmentName;
		this.coordinate = coordinate;
		this.id = id;
	}

	public Destination(){}
}
