package com.example.LogGuard.Service;

import com.example.LogGuard.Config.DelayedConnectionDataSource;
import com.example.LogGuard.LogGuardApplication;
import com.example.LogGuard.Model.User;
import com.example.LogGuard.Repository.UserDao;
import org.hibernate.QueryTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Optional;

import java.sql.Connection;
import java.sql.SQLException;

@Service
public class UserServiceImpl implements UserService{
    private static final Logger log = LoggerFactory.getLogger(LogGuardApplication.class);

    @Value("spring.datasource.url")
    private String dbConnectionUrl;

    private DelayedConnectionDataSource delayedConnectionDataSource = new DelayedConnectionDataSource();

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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
    @Cacheable(value = "user",key = "#mid")
    public User fetchUserById(int mid,Boolean check) throws SQLException, InterruptedException {
        Long startTime = System.currentTimeMillis();
        if(check.equals(true)){
            Thread.sleep(5000L);
        }
        Optional<User> user =  userDao.findById(mid);
        if(user != null){
            logResponseTime(startTime,user.get(),check);
        }
        return user.get();
    }

    @Override
    public Boolean userExistById(int mid) {
        return userDao.existsById(mid);
    }

    @Override
    public void deleteById(User user) {
        String firstName = user.getFirstName();
        log.info("User " + firstName +" Deleted from Db.");
        userDao.deleteById(user.getMid());
    }


    @Override
    public void failedConnectionToDb() throws SQLException {
        try{
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl("jdbc:mysql://localhost:3306/wrongDb");
            dataSource.setUsername("root");
            dataSource.setPassword("Phoenix123$");
            Connection connection = dataSource.getConnection();
        }
        catch(Exception e){
            log.error("An error occurred while connecting to the Database server: Invalid Database. " + e);
        };
    }

    @Override
    public void failedConnectionToCache() {
        try{
            String host = "127.0.0.3";
            int port = 6379;
            Jedis jedis = new Jedis(host, port);
            log.info("Redis cache connection successful.");
            jedis.ping();
            jedis.close();
        }
        catch(Exception e){
            log.error("An error occurred while connecting to the Redis server: SocketTimeoutException: connect timed out. " + e);
        };
    }

    @Override
    public void dbConnectonTimeOut() throws SQLException, InterruptedException {
        try{
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl("jdbc:mysql://localhost:3306/logDb");
            dataSource.setUsername("root");
            dataSource.setPassword("Phoenix123$");
            Connection connection = delayedConnectionDataSource.getConnection();
            log.info("Db connected successfully");
        }
        catch(Exception e){
            log.error("An error occurred while establishing a database connection: Connection timeout. ", e.getMessage());
        }
    }

    @Override
    public Boolean userExistInCache(int mid){
        return redisTemplate.hasKey("user::"+String.valueOf(mid));
    }

    @Override
    public void redisTimeOut() {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            try {
                log.info("Redis operation started.");
                Thread.sleep(10000L);
                log.info("Redis operation completed in 10000s.");
            } catch (InterruptedException e) {
                log.error("Redis operation interrupted. " , e);
            } catch (QueryTimeoutException q){
                log.error("Redis operation timed out. " , q);
            }
            return null;
        });
    }

    private void logResponseTime(Long startTime,User user,Boolean check){
        Long dbResponseTime = System.currentTimeMillis() - startTime;
        log.info("User " + user.getFirstName() + " fetched from database in time " + dbResponseTime +"ms");
        if(check.equals(true)) log.info("Increased latency of Database operation.");
    }

}
