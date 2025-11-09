package com.app.demo.Controller;

import java.util.List;

import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.demo.DTO.Request.RoleRequest;
import com.app.demo.DTO.Response.RoleResponse;
import com.app.demo.Service.RoleService;

@RestController
@RequestMapping("/api/role")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService service;

    public RoleController(RoleService service) {
        this.service = service;
    }

    @PostMapping("/create-role")
    public ResponseEntity<String> createRole(@RequestBody RoleRequest request, Authentication authentication) {
        service.createRole(request);
        return ResponseEntity.ok("Role creado");
    }

    @GetMapping("/get-roles")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/get-role/{rol_id}")
    public ResponseEntity<RoleResponse> getById(@PathVariable Long rol_id) {
        return ResponseEntity.ok(service.getById(rol_id));
    }

    @PutMapping("/update-role/{rol_id}")
    public ResponseEntity<RoleResponse> updateRole(
            @PathVariable Long rol_id,
            @RequestBody RoleRequest request) {

        RoleResponse response = service.updateRole(rol_id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-role/{rol_id}")
    public ResponseEntity<String> deleteRole(@PathVariable Long rol_id) {
        service.deleteRole(rol_id);
        return ResponseEntity.ok("Role eliminado");
    }

}
