package com.sougane.users_microservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sougane.users_microservice.entities.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long>{
	
	Utilisateur findByUsername(String username);
	
	Optional<Utilisateur> findByEmail(String email);
	
}
