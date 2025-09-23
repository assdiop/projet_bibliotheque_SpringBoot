package com.sougane.users_microservice.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sougane.users_microservice.entities.Role;
import com.sougane.users_microservice.entities.Utilisateur;
import com.sougane.users_microservice.repository.RoleRepository;
import com.sougane.users_microservice.repository.UtilisateurRepository;
import com.sougane.users_microservice.services.exceptions.EmailAlreadyExistsException;
import com.sougane.users_microservice.services.register.RegistrationRequest;

@Transactional
@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	UtilisateurRepository utilisateurRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	

	@Override
	public Utilisateur saveUtilisateur(Utilisateur utilisateur) {
		
		utilisateur.setPassword(bCryptPasswordEncoder.encode(utilisateur.getPassword()));
		return utilisateurRepository.save(utilisateur); 
	} 

	@Override
	public Utilisateur findUserByUsername(String username) {
		
		return utilisateurRepository.findByUsername(username);
	}

	@Override
	public Role addRole(Role role) {
		
		return roleRepository.save(role);
	}

	@Override
	@Transactional
	public Utilisateur addRoleToUser(String username, String rolename) {
	    Utilisateur usr = utilisateurRepository.findByUsername(username);
	    if (usr == null) {
	        throw new RuntimeException("Utilisateur " + username + " introuvable");
	    }

	    Role role = roleRepository.findByRole(rolename);
	    if (role == null) {
	        throw new RuntimeException("Rôle " + rolename + " introuvable");
	    }

	    // Vérifie si l'utilisateur n'a pas déjà ce rôle
	    boolean hasRole = usr.getRoles().stream()
	                         .anyMatch(r -> r.getRole().equalsIgnoreCase(rolename));

	    if (!hasRole) {
	        usr.getRoles().add(role);
	        usr = utilisateurRepository.save(usr); //sauvegarde
	    }

	    return usr;
	}
	
	@Override
	@Transactional
	public Utilisateur removeRoleFromUser(String username, String rolename) {
	    Utilisateur usr = utilisateurRepository.findByUsername(username);
	    if (usr == null) throw new RuntimeException("Utilisateur " + username + " introuvable");

	    Role role = roleRepository.findByRole(rolename);
	    if (role == null) throw new RuntimeException("Rôle " + rolename + " introuvable");

	    boolean hasRole = usr.getRoles().stream()
	            .anyMatch(r -> r.getRole().equalsIgnoreCase(rolename));

	    if (hasRole) {
	        usr.getRoles().removeIf(r -> r.getRole().equalsIgnoreCase(rolename));
	        usr = utilisateurRepository.save(usr);
	    }

	    return usr;
	}



	@Override
	public List<Utilisateur> findAllUsers() {
		
		return utilisateurRepository.findAll();
	}

	@Override
	public Utilisateur registerUser(RegistrationRequest request) {
		Optional<Utilisateur> optionaluser = utilisateurRepository.findByEmail(request.getEmail());
		if(optionaluser.isPresent()) {
			throw new EmailAlreadyExistsException("email déjà existant!");
		}
		
		Utilisateur newUser = new Utilisateur();
		newUser.setUsername(request.getUsername());
		newUser.setEmail(request.getEmail());
		newUser.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
		newUser.setEnabled(true);
		utilisateurRepository.save(newUser);
			
		//ajouter à newUser le role par défaut USER
		Role r = roleRepository.findByRole("USER");
		List<Role> roles = new ArrayList<>();
		roles.add(r);
		newUser.setRoles(roles);
		
		return utilisateurRepository.save(newUser);
		
	}

	@Override
	public Utilisateur desactiverUtilisateur(Long id) {
	    Utilisateur utilisateur = utilisateurRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("L'utilisateur avec id " + id + " n'existe pas."));

	    utilisateur.setEnabled(false); //on désactive
	    return utilisateurRepository.save(utilisateur);
	}

	@Override
	public Utilisateur activerUtilisateur(Long id) {
	    Utilisateur utilisateur = utilisateurRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("L'utilisateur avec id " + id + " n'existe pas."));
	    utilisateur.setEnabled(true);
	    return utilisateurRepository.save(utilisateur);
	}
	
	
	@Override
	@Transactional
	public void supprimerUtilisateur(Long id) {
	    Utilisateur u = utilisateurRepository.findById(id)
	        .orElseThrow(() -> new RuntimeException("L'utilisateur avec id " + id + " n'existe pas."));

	    //  Utile si ManyToMany sans cascade REMOVE
	    if (u.getRoles() != null) {
	        u.getRoles().clear();
	    }

	    utilisateurRepository.delete(u);
	}
	
}
