package fr.lino.layani.lior.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.lino.layani.lior.repository.DoctorRepository;
import fr.lino.layani.lior.repository.VisitRepository;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class LoadDatabase {

	@Bean
	CommandLineRunner initDatabase(DoctorRepository doctorRepository, VisitRepository visitRepository) {

		return args -> {

		};
	}
}
