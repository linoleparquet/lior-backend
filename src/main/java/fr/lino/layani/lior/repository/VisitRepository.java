package fr.lino.layani.lior.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.lino.layani.lior.model.Visit;

public interface VisitRepository extends JpaRepository<Visit, Integer> {

	List<Visit> findByDoctorId(int id);

}
