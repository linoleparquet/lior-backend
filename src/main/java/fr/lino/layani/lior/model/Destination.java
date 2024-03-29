package fr.lino.layani.lior.model;

import java.time.LocalTime;

import com.graphhopper.jsprit.core.util.Coordinate;

import lombok.Data;

@Data
public class Destination implements Cloneable {

	String id;
	String establishmentName;
	String doctorName;
	String horaires;
	String address;
	Coordinate coordinate;
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

	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}

}
