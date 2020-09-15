package fr.lino.layani.lior.service;

import java.util.List;

import fr.lino.layani.lior.model.Doctor;

public interface DoctorService {
	List<Doctor> getAllDoctor();

	Doctor postCreateNewDoctor(Doctor doctor);

	Doctor getOneDoctor(int id);

	Doctor putUpdateOneDoctor(Doctor doctor, int id);

	void deleteOneDoctor(int id);

	void updateNextVisit(Doctor doctor);

}
