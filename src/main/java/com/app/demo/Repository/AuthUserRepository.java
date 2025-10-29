package com.app.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.demo.Entity.AuthUser;
import com.app.demo.Entity.User;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {

	@Query("""
			    SELECT au
			    FROM AuthUser au
			    JOIN FETCH au.role
			    JOIN FETCH au.user
			    WHERE au.username = :username
			""")
	Optional<AuthUser> findByUsernameWithRoleAndUser(@Param("username") String username);

	Optional<AuthUser> findByUsername(String username);

	Optional<AuthUser> findByUser(User savedUser);

}
