package com.example.multilab.Repositories;

import com.example.multilab.Entities.User;
import com.example.multilab.Entities.UserFCMToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserFCMTokenRepo extends JpaRepository<UserFCMToken, Integer> {
    Optional<UserFCMToken> findByUser(User user);
    Optional<UserFCMToken> findByUserId(int userId);
}
