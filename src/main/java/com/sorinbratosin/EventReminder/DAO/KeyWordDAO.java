package com.sorinbratosin.EventReminder.DAO;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface KeyWordDAO extends CrudRepository<Keyword, Integer> {



    public List<Keyword> findAllByKeyword(String keyword);

}
