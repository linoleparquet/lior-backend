package fr.lino.layani.lior.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.lino.layani.lior.model.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

}
