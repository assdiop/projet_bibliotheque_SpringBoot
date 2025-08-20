package sn.unchk.bibliotheque;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


//@EntityScan("sn.unchk.bibliotheque.entity") // Scan des entités
//@EnableJpaRepositories("sn.unchk.bibliotheque.repository") // Scan des repositories

@SpringBootApplication
public class BibliothequeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BibliothequeApplication.class, args);
	}

}
