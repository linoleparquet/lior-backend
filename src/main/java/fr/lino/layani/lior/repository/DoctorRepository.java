package fr.lino.layani.lior.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.lino.layani.lior.model.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

	List<Doctor> findByEstablishmentId(int id);

}
