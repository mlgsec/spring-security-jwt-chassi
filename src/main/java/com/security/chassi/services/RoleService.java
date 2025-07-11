package com.security.chassi.services;

import com.security.chassi.repositories.RoleRepository;
import com.security.chassi.entities.Role;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Role> getAllRoles() {
        return repository.findAll();
    }

    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Role com ID " + id + " não encontrada.");
        }
        repository.deleteById(id);
    }

    public Role createRole(Role role) {
        return repository.save(role);
    }

}
