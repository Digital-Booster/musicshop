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
/* 
	@Bean
	public CommandLineRunner demo(CustomerRepository repository) {
		return (args) -> {
			List<Customer> customers = repository.findAll();
			
			if (customers.isEmpty()) {
				log.info("No customers found in the database.");
				return;
			}

			// Print header
			log.info("\nCustomer List");
			log.info("=============");
			String format = "%-4s | %-20s | %-20s | %-30s | %-15s | %-20s";
			log.info(String.format(format, "ID", "First Name", "Last Name", "Email", "Country", "City"));
			log.info("-".repeat(100));

			// Print each customer
			customers.forEach(customer -> {
				log.info(String.format(format,
					customer.getCustomerId(),
					truncateString(customer.getFirstName(), 20),
					truncateString(customer.getLastName(), 20),
					truncateString(customer.getEmail(), 30),
					truncateString(customer.getCountry(), 15),
					truncateString(customer.getCity(), 20)
				));
			});
			log.info("-".repeat(100));
			log.info("Total Customers: " + customers.size());
			log.info("");
		};
	}

	private String truncateString(String str, int maxLength) {
		if (str == null) return "";
		return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
	}
		*/
}