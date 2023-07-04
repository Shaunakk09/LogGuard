package com.example.LogGuard.Service;

import com.example.LogGuard.Model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserService {
    void addUser(User user);
    List<User> fetchAllUser();
    User fetchUserById(int mid,Boolean check) throws SQLException;
    Boolean userExistById(int mid);
    void deleteById(User user);
    void redisTimeOut();
    void failedConnectionToDb() throws SQLException;
    void failedConnectionToCache();
    void dbConnectonTimeOut() throws SQLException, ClassNotFoundException, InterruptedException;
}
