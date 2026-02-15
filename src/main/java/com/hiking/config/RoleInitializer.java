package com.hiking.config;

import com.hiking.user.entity.Role;
import com.hiking.user.entity.RoleType;
import com.hiking.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        for (RoleType roleType : RoleType.values()) {

            if (!roleRepository.existsByName(roleType.name())) {

                Role role = new Role();
                role.setName(roleType.name());

                roleRepository.save(role);

                System.out.println("Created role: " + roleType.name());
            }
        }
    }
}