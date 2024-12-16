package com.example.multilab.Services;

import com.example.multilab.Entities.User;
import com.example.multilab.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public User AddUser(User user){
        return userRepo.save(user);
    }

    public void DeleteUser(int IdUser){
        User user = userRepo.findById(IdUser).get();

        userRepo.delete(user);
    }

    public User UpdateUser(int IdUser, User user)
    {
        User u = userRepo.findById(IdUser).get();

        u.setNom(user.getNom());
        u.setPrenom(user.getPrenom());
        u.setUsername(user.getUsername());
        u.setPwd(user.getPwd());

        return userRepo.save(u);
    }

    public User ShowUser(int IdUser)
    {
        return userRepo.findById(IdUser).get();
    }

    public List<User> ShowAllUsers()
    {
        return userRepo.findAll();
    }
}
