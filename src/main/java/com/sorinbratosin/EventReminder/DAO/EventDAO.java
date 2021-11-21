package com.sorinbratosin.EventReminder.DAO;


import org.springframework.data.repository.Repository;

import java.util.List;

public interface EventDAO extends Repository<Event, Integer> {

    public List<Event> findAll();

    public void save(Event event);

    public Integer countAllByName(String name);

    public List<Event> findByLocation(String location);

    public List<Event> findByLocationAndDateStart(String location, String date);

    public List<Event> findByLocationContainingOrCityContainingOrNameContainingOrGenreContaining(String location, String city, String name, String genre);
}
