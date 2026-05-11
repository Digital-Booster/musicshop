package net.digitalbooster.musicshop;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.digitalbooster.musicshop.model.Customer;
import net.digitalbooster.musicshop.repository.CustomerRepository;
import java.util.List;

@SpringBootApplication
public class MusicshopApplication {
	private static final Logger log = LoggerFactory.getLogger(MusicshopApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(MusicshopApplication.class, args);
	}
}