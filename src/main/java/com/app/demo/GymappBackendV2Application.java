package com.app.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.app.demo.Entity.Role;
import com.app.demo.Repository.RoleRepository;

@SpringBootApplication
public class GymappBackendV2Application {

	public static void main(String[] args) {
		SpringApplication.run(GymappBackendV2Application.class, args);
	}

	// Este es el CommandLineRunner para inicializar roles
    @Bean
    public CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.count() == 0) {
                Role admin = new Role();
                admin.setName("ADMIN");
                admin.setDescription("Administrador");

                Role user = new Role();
                user.setName("USER");
                user.setDescription("Usuario estándar");

                roleRepository.save(admin);
                roleRepository.save(user);
            }
        };
    }

}
