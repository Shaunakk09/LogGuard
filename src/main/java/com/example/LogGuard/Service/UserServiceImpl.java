package com.example.LogGuard.Service;

import com.example.LogGuard.Model.User;
import com.example.LogGuard.Repository.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserDao userDao;

    @Override
    public void addUser(User user) {
        userDao.save(user);
    }

    @Override
    public List<User> fetchAllUser() {
        return (List<User>) userDao.findAll();
    }

    @Override
    public Optional<User> fetchUserById(int mid) {
        return userDao.findById(mid);
    }

    @Override
    public Boolean userExistById(int mid) {
        return userDao.existsById(mid);
    }

    @Override
    public void deleteById(int mid) {
        userDao.deleteById(mid);
    }


}
