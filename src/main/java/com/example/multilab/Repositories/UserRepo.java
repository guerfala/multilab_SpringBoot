package com.example.multilab.Repositories;

import com.example.multilab.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Integer> {
    Optional<User> findByUsernameAndPwd(String Username, String pwd);
}
