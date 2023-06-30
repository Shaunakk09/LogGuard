package com.example.LogGuard.Service;

import com.example.LogGuard.Model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void addUser(User user);
    List<User> fetchAllUser();
    Optional<User> fetchUserById(int mid);
    Boolean userExistById(int mid);
    void deleteById(int mid);
}
