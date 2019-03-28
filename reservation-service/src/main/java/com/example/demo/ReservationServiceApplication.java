package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Collection;
import java.util.stream.Stream;

@SpringBootApplication
public class ReservationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationServiceApplication.class, args);
	}

}

@Component
class Initializer implements ApplicationRunner {

	private final ReservationRepository reservationRepository;

	public Initializer(ReservationRepository reservationRepository) {
		this.reservationRepository = reservationRepository;
	}

	@Override
	public void run(ApplicationArguments applicationArguments) throws Exception {
		Stream.of("Ali", "Bilal", "Cemal", "Davut").forEach( n ->  reservationRepository.save(new Reservation(null, n)) );

		reservationRepository.findAll().forEach(System.out::println);

	}
}

@Slf4j
@RestController
class ReservationController {

	@Autowired
	Environment environment;

	private final ReservationRepository reservationRepository;

	public ReservationController(ReservationRepository reservationRepository) {
		this.reservationRepository = reservationRepository;
	}

	@GetMapping("/reservations")
	public Collection<Reservation> reservations() {
		log.info("{} server", environment.getProperty("server.port"));
		return this.reservationRepository.findAll();
	}
}

interface ReservationRepository extends JpaRepository<Reservation, Long> {
	Collection<Reservation> findByReservationName(String rn);
}

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
class Reservation {
	@Id
	@GeneratedValue
	private Long id;
	private String reservationName;
}