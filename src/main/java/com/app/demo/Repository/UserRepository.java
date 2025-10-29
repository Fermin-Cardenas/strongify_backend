package com.app.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.demo.Entity.User;

public interface UserRepository extends JpaRepository<User, Long>{

}
