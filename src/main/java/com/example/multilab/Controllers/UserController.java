package com.example.multilab.Controllers;

import com.example.multilab.Entities.User;
import com.example.multilab.Services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/multilab")
@CrossOrigin("http://localhost:4200")
@AllArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    // http://localhost:8081/multilab/AddUser
    @PostMapping("/AddUser")
    public User AddUser(@RequestBody User user){
        return userService.AddUser(user);
    }

    // http://localhost:8081/multilab/DeleteUser/{IdUser}
    @DeleteMapping("/DeleteUser/{IdUser}")
    public void DeleteUser(@PathVariable int IdUser){
        userService.DeleteUser(IdUser);
    }

    // http://localhost:8081/multilab/UpdateUser/{IdUser}
    @PutMapping("/UpdateUser/{IdUser}")
    public User UpdateUser(@PathVariable int IdUser, @RequestBody User user){
        return userService.UpdateUser(IdUser, user);
    }

    // http://localhost:8081/multilab/ShowUser/{IdUser}
    @GetMapping("/ShowUser/{IdUser}")
    public User ShowUser(@PathVariable int IdUser){
        return userService.ShowUser(IdUser);
    }

    // http://localhost:8081/multilab/ShowAllUsers
    @GetMapping("/ShowAllUsers")
    public List<User> ShowAllUsers(){
        return userService.ShowAllUsers();
    }

}
