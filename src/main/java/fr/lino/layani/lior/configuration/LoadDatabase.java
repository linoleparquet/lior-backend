package fr.lino.layani.lior.configuration;

import fr.lino.layani.lior.model.*;
import fr.lino.layani.lior.repository.EstablishmentRepository;
import fr.lino.layani.lior.repository.UserPreferenceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.lino.layani.lior.repository.DoctorRepository;
import fr.lino.layani.lior.repository.VisitRepository;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
@Slf4j
public class LoadDatabase {

	@Bean
	CommandLineRunner initDatabase(DoctorRepository doctorRepository, VisitRepository visitRepository, EstablishmentRepository establishmentRepository, UserPreferenceRepository userPreferenceRepository) {
		return args -> {

			// ------------------------ Initialise establishments ------------------------
			Establishment establishment1 = new Establishment();
			establishment1.setId(1);
			establishment1.setDepartment("31, Haute-Garonne, Occitanie");
			establishment1.setName("Hopital Pierre Paul Riquet");
			establishment1.setAddress("Allée Jean Dausset, 31300 Toulouse, France");
			establishment1.setCity("Toulouse");
			establishment1.setX(1.401649);
			establishment1.setY(43.612173);
			establishmentRepository.save(establishment1);

			Establishment establishment2 = new Establishment();
			establishment2.setId(2);
			establishment2.setDepartment("32, Gers, Occitanie");
			establishment2.setName("Centre Hospitalier d'Auch");
			establishment2.setAddress("Allée Marie Clarac, 32008 Auch, France");
			establishment2.setCity("Auch");
			establishment2.setX(0.578768);
			establishment2.setY(43.630208);
			establishmentRepository.save(establishment2);

			Establishment establishment3 = new Establishment();
			establishment3.setId(3);
			establishment3.setDepartment("33, Gironde, Nouvelle-Aquitaine");
			establishment3.setName("Hôpital Pellegrin");
			establishment3.setAddress("Rue de la Pelouse de Douet, 33000 Bordeaux, France");
			establishment3.setCity("Bordeaux");
			establishment3.setX(-0.607249);
			establishment3.setY(44.829942);
			establishmentRepository.save(establishment3);

			Establishment establishment4 = new Establishment();
			establishment4.setId(4);
			establishment4.setDepartment("75, Paris, Île-de-France");
			establishment4.setName("Hôpital Saint-Joseph");
			establishment4.setAddress("185 Rue Raymond Losserand");
			establishment4.setCity("Paris");
			establishment4.setX(2.310073);
			establishment4.setY(48.829796);
			establishmentRepository.save(establishment4);

			Establishment establishment5 = new Establishment();
			establishment5.setId(5);
			establishment5.setDepartment("47, Lot-et-Garonne, Nouvelle-Aquitaine");
			establishment5.setName("Centre Hospitalier Agen-Nérac (site d’Agen)");
			establishment5.setAddress("80 Allées d’Albret");
			establishment5.setCity("Nérac");
			establishment5.setX(0.336615);
			establishment5.setY(44.135947);
			establishmentRepository.save(establishment5);

			// ------------------------ Initialize doctors ------------------------
			Doctor doctor1 = new Doctor();
			doctor1.setName("Faritet");
			doctor1.setSurname("Jean");
			doctor1.setEstablishment(establishment1);
			doctor1.setPeriodicity(1);
			doctorRepository.save(doctor1);

			Doctor doctor2 = new Doctor();
			doctor2.setName("Dubois");
			doctor2.setSurname("Maxime");
			doctor2.setEstablishment(establishment2);
			doctor2.setPeriodicity(2);
			doctorRepository.save(doctor2);

			Doctor doctor3 = new Doctor();
			doctor3.setName("Perez");
			doctor3.setSurname("Ella");
			doctor3.setEstablishment(establishment3);
			doctor3.setPeriodicity(6);
			doctorRepository.save(doctor3);

			Doctor doctor4 = new Doctor();
			doctor4.setName("Matignon");
			doctor4.setSurname("Sylvie");
			doctor4.setEstablishment(establishment4);
			doctor4.setPeriodicity(6);
			doctorRepository.save(doctor4);

			Doctor doctor5 = new Doctor();
			doctor5.setName("Levy");
			doctor5.setSurname("Thomas");
			doctor5.setEstablishment(establishment5);
			doctor5.setPeriodicity(4);
			doctorRepository.save(doctor5);

			// ------------------------ Initialize Visits ------------------------
			Visit visit11 = new Visit();
			visit11.setDate(LocalDate.now().minusDays(2));
			visit11.setDoctor(doctor1);
			visit11.setNotes("Think about make him sign the patient XXX.");
			visitRepository.save(visit11);

			Visit visit21 = new Visit();
			visit21.setDate(LocalDate.now().minusMonths(3));
			visit21.setDoctor(doctor2);
			visit21.setNotes("Her secretary is called Natacha. She was really nice with me this time.");
			visitRepository.save(visit21);

			Visit visit31 = new Visit();
			visit31.setDate(LocalDate.now().minusMonths(12));
			visit31.setDoctor(doctor3);
			visit31.setNotes("Her husband is called Jerôme. And he likes wine.");
			visitRepository.save(visit31);

			Visit visit41 = new Visit();
			visit41.setDate(LocalDate.now().minusMonths(10));
			visit41.setDoctor(doctor4);
			visit41.setNotes("His colleague Franck Bergniolles is interested by our services. His number: 06 XX XX XX XX");
			visitRepository.save(visit41);

			Visit visit51 = new Visit();
			visit51.setDate(LocalDate.now().minusMonths(4));
			visit51.setDoctor(doctor5);
			visitRepository.save(visit51);

			// ------------------------ Initialize User Preference ------------------------

			UserLocation userLocation = new UserLocation();
			userLocation.setId(1);
			userLocation.setName("Place du Capitole");
			userLocation.setX(1.440694);
			userLocation.setY(43.603902);

			UserPreference userPreference = new UserPreference();
			userPreference.setId(1);
			userPreference.setName("default");
			userPreference.setUserLocation(userLocation);
			userPreference.setEarliestStart(LocalTime.parse("09:00"));
			userPreference.setLatestArrival(LocalTime.parse("18:00"));
			userPreference.setWaitingTime(LocalTime.parse("00:30"));
			userPreference.setMaxDestinationPerDay(200);

			userPreferenceRepository.save(userPreference);

		};
	}
}
