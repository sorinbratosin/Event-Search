package com.sorinbratosin.EventReminder.DAO;

import org.springframework.data.repository.Repository;

import java.util.List;


public interface UserDAO extends Repository<User, Integer> {

    public void save(User user);

    public List<User> searchUserByEmail(String email);

}
