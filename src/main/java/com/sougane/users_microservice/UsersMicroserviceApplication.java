package com.sougane.users_microservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.sougane.users_microservice.entities.Role;
import com.sougane.users_microservice.entities.Utilisateur;
import com.sougane.users_microservice.services.UserService;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class UsersMicroserviceApplication {

	@Autowired
	UserService userService;
	
	public static void main(String[] args) {
		SpringApplication.run(UsersMicroserviceApplication.class, args);
	}
	
	
	/*@PostConstruct
	void init_users() {
		//ajouter les rôles
		userService.addRole(new Role(null,"ADMIN"));
		userService.addRole(new Role(null,"USER"));
		
		//ajouter les users
		userService.saveUtilisateur(new Utilisateur(null,"admin","123",true,"admin@gmail.com",null));	
		userService.saveUtilisateur(new Utilisateur(null,"abdallah","123",true,"abdallah@gmail.com",null));
		userService.saveUtilisateur(new Utilisateur(null,"bigool","123",true,"bigool@gmail.com",null));
		
		//ajouter les rôles aux users
		userService.addRoleToUser("admin", "ADMIN");
		userService.addRoleToUser("admin", "USER");
		userService.addRoleToUser("abdallah", "USER");
		userService.addRoleToUser("bigool", "USER");
	
	}*/
	
	
}
