package com.security.chassi.services;

import com.security.chassi.repositories.RoleRepository;
import com.security.chassi.user.Role;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleRepository repository;

    public RoleService(RoleRepository roleRepository) {
        this.repository = roleRepository;
    }

    public Role findByName(String name) {
        return repository.findByName(name).orElseThrow(
                () -> new RuntimeException("Permissão não exitente.")
        );
    }
}