package org.example.chuyendeweb_be;

import org.example.chuyendeweb_be.entity.Role;
import org.example.chuyendeweb_be.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ChuyenDeWebBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChuyenDeWebBeApplication.class, args);
	}
	@Bean
	CommandLineRunner initDefaultRoles(RoleRepository roleRepository) {
		return args -> {
			if (roleRepository.findByRoleName("ROLE_CLIENT").isEmpty()) {
				roleRepository.save(new Role(null, "ROLE_CLIENT"));
			}
		};
	}
}
