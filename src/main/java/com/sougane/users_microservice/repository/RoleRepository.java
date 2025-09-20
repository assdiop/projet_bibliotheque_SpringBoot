package com.sougane.users_microservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sougane.users_microservice.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{
	
	Role findByRole(String role);
}
