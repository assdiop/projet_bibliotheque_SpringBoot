package com.sougane.users_microservice.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sougane.users_microservice.entities.Utilisateur;
import com.sougane.users_microservice.services.UserService;

@Service
public class MyUserDetailsService implements UserDetailsService {
	
	@Autowired
	UserService userService;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Utilisateur user = userService.findUserByUsername(username);
		if (user==null) {
			throw new UsernameNotFoundException("Utilisateur introuvable !");
		}
		
		List<GrantedAuthority> auths = new ArrayList<>();
			user.getRoles().forEach(role -> {
			GrantedAuthority auhority = new SimpleGrantedAuthority(role.getRole());
			auths.add(auhority);
		});
		
		return new User(user.getUsername(),user.getPassword(),user.getEnabled(),true,true,true,auths);
	}

}
