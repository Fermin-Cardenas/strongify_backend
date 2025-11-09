package com.app.demo.Service;

import java.util.List;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.springframework.stereotype.Service;

import com.app.demo.DTO.Request.RoleRequest;
import com.app.demo.DTO.Response.RoleResponse;
import com.app.demo.Entity.Role;
import com.app.demo.Repository.RoleRepository;

import jakarta.transaction.Transactional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional
    public RoleResponse createRole(RoleRequest request) {

        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role = roleRepository.save(role);

        RoleResponse response = new RoleResponse(
                role.getName(),
                role.getDescription());
        return response;
    }

    public List<RoleResponse> getAll() {

        List<Role> roles = roleRepository.findAll();

        return roles.stream()
                .map(role -> new RoleResponse(role.getName(), role.getDescription())).toList();
    }

    public RoleResponse getById(Long role_id) {

        Role role = roleRepository.findById(role_id)
                .orElseThrow(() -> new RuntimeException("Role no encontrado"));

        return new RoleResponse(role.getName(), role.getDescription());
    }

    @Transactional
    public RoleResponse updateRole(Long rol_id, RoleRequest request) {

        Role role = roleRepository.findById(rol_id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            role.setName(request.getName());
        }

        if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
            role.setDescription(request.getDescription());
        }

        Role update = roleRepository.save(role);
        return new RoleResponse(
                update.getName(), update.getDescription());
    }

    @Transactional
    public void deleteRole(Long rol_id){
        Role role = roleRepository.findById(rol_id)
        .orElseThrow(()-> new RuntimeException("Role not found"));

        roleRepository.delete(role);
    }

}
