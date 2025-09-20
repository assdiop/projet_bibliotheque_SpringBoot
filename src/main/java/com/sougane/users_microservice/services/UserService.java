package com.sougane.users_microservice.services;

import java.util.List;

import com.sougane.users_microservice.entities.Role;
import com.sougane.users_microservice.entities.Utilisateur;
import com.sougane.users_microservice.services.register.RegistrationRequest;

public interface UserService {
	
	Utilisateur saveUtilisateur(Utilisateur utilisateur); 
	
	Utilisateur findUserByUsername (String username);
	
	List<Utilisateur> findAllUsers();
	
	Role addRole(Role role); 
	
	Utilisateur addRoleToUser(String username, String rolename);
	
	Utilisateur removeRoleFromUser(String username, String rolename);
	
	
	Utilisateur registerUser(RegistrationRequest request);	
	
	Utilisateur desactiverUtilisateur(Long id);
	
	Utilisateur activerUtilisateur(Long id);
	
	public void supprimerUtilisateur(Long id);

}
