package fr.lino.layani.lior.service;

import java.util.List;

import fr.lino.layani.lior.model.Doctor;
import fr.lino.layani.lior.model.Visit;

public interface DoctorService {
	List<Doctor> getAllDoctor();

	Doctor postCreateNewDoctor(Doctor newDoctor);

	Doctor getOneDoctor(int id);

	Doctor putUpdateOneDoctor(Doctor updatedDoctor, int id);

	void deleteOneDoctor(int id);

	void updateLastVisitNextVisitField(int id);

	public List<Visit> getVisitByDotor(int id);

}
