package com.rtseki.witch.backend.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rtseki.witch.backend.domain.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByEmail(String email);
	
	Optional<User> findByUserId(String userId);
	
	@Query("SELECT new com.rtseki.witch.backend.domain.model.User(userId, firstname, lastname, email) FROM User WHERE email = ?1")
	User findByEmailToCurrentUser(String email);
}
