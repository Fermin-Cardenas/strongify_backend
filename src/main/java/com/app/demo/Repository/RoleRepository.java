package com.app.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.demo.Entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{

}
