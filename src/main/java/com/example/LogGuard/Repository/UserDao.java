package com.example.LogGuard.Repository;

import com.example.LogGuard.Model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserDao extends CrudRepository<User, Integer> {
}
