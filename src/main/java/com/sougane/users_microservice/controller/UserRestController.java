package com.sougane.users_microservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sougane.users_microservice.entities.Utilisateur;
import com.sougane.users_microservice.services.UserService;
import com.sougane.users_microservice.services.register.RegistrationRequest;

@RestController
@CrossOrigin(origins = "*")
public class UserRestController {
	
	@Autowired
	UserService userService;
	
	@GetMapping("all")
	public List<Utilisateur> getAllUsers() {
		return userService.findAllUsers();
	}
	
	@PostMapping("/register")
	public Utilisateur register(@RequestBody RegistrationRequest request) {
		return userService.registerUser(request);
	}

	@PutMapping("/desactiver/{id}")
	public ResponseEntity<Utilisateur> desactiverUtilisateur(@PathVariable Long id) {
	    Utilisateur updatedUser = userService.desactiverUtilisateur(id);
	    return ResponseEntity.ok(updatedUser);
	}
	
	@PutMapping("/activer/{id}")
	public ResponseEntity<Utilisateur> activerUtilisateur(@PathVariable Long id) {
	    return ResponseEntity.ok(userService.activerUtilisateur(id));
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> supprimer(@PathVariable Long id) {
		userService.supprimerUtilisateur(id);
	    return ResponseEntity.noContent().build(); // 204
	}
	
	@PostMapping("/addRole/{username}/roles/{rolename}")
	public ResponseEntity<Utilisateur> addRoleToUser(
	        @PathVariable String username,
	        @PathVariable String rolename) {
	    Utilisateur updatedUser = userService.addRoleToUser(username, rolename);
	    return ResponseEntity.ok(updatedUser);
	}
	
	
	@DeleteMapping("/removeRole/{username}/roles/{rolename}")
	public ResponseEntity<Utilisateur> removeRoleFromUser(
	        @PathVariable String username,
	        @PathVariable String rolename) {
	    Utilisateur updatedUser = userService.removeRoleFromUser(username, rolename);
	    return ResponseEntity.ok(updatedUser);
	}


	
}
