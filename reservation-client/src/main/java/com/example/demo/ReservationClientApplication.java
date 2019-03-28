package com.example.demo;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@EnableFeignClients
@SpringBootApplication
public class ReservationClientApplication {

	@Bean
	@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(ReservationClientApplication.class, args);
	}

}

@FeignClient("reservation-service")
interface ReservationClient {
	@GetMapping("/reservations")
	Collection<Reservation> read();
}

@RestController
class ReservationApiAdapterRestController {
/*
	private final RestTemplate	restTemplate;

	public ReservationApiAdapterRestController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
*/

	private final ReservationClient client;

	public ReservationApiAdapterRestController(ReservationClient client) {
		this.client = client;
	}

	public Collection<String> fallback() {
		return new ArrayList<>();
	}

	@HystrixCommand(fallbackMethod = "fallback")
	@GetMapping("/reservations/names")
	public Collection<String> names() {
//		return this.restTemplate.exchange("http://reservation-service/reservations", );
		return this.client
				.read()
				.stream()
				.map(Reservation::getReservationName)
				.collect(Collectors.toList());
	}
}