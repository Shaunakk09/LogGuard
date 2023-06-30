package com.example.LogGuard.Controller;

import com.example.LogGuard.LogGuardApplication;
import com.example.LogGuard.Model.User;
import com.example.LogGuard.Service.UserService;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
public class GenericController {
    private static final Logger log = LoggerFactory.getLogger(LogGuardApplication.class);

    @Autowired
    private UserService userService;

    @PostMapping("/user")
    public void saveUser(@RequestBody User user){
        userService.addUser(user);
        log.info("User " + user.getFirstName() + " added to the database.");
    }

    @GetMapping("/user")
    public List<User> fetchAllUser(){
        List<User> users = userService.fetchAllUser();
        for(User user:users){
            String firstName = user.getFirstName();
            Integer mid = user.getMid();
            log.info("User "+mid.toString()+" -> "+firstName);
        }
        return users;
    }

    @GetMapping("/user/{mid}")
    @Cacheable(value = "user",key = "#mid")
    public User fetchUserbyId(@PathVariable(value = "mid") Integer mid) {
        User user = userService.fetchUserById(mid).get();
        log.info("User " + user.getFirstName() + " fetched from the database.");
        return user;
    }

    @DeleteMapping("/user/{mid}")
    @CacheEvict(value = "user",key = "#mid")
    public ResponseEntity<String> deleteUser(@PathVariable("mid") Integer mid){
        if(userService.userExistById(mid)) {
            Optional<User> user = userService.fetchUserById(mid);
            String firstName = user.get().getFirstName();
            userService.deleteById(mid);
            log.info("User " + firstName +" Deleted from Db.");
            return ResponseEntity.ok("User Deleted successfully");
        }
        else  return  ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PutMapping("/user")
    @CachePut(value = "user",key = "#user.getMid()")
    public void updateUser(@RequestBody User user){
         userService.addUser(user);
    }

    @PostMapping("/add/dummy/user")
    public void saveDummyUser(){
        for(int i = 1;i <= 100; i++){
            Faker faker = new Faker();
            Random r = new Random();
            User user = new User(i,faker.name().firstName(),faker.name().lastName(),r.nextInt(100-1)+1);
            userService.addUser(user);
        }
    }
}
