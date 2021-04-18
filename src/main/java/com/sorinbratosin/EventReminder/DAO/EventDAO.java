package com.sorinbratosin.EventReminder.DAO;


import org.springframework.data.repository.Repository;

import java.util.List;

public interface EventDAO extends Repository<Event, Integer> {

    //foloseste o conventie de a numi metodele pentru a scrie automat cod SQL pt noi si a ne da ceea ce cerem - SPRING DATA JPA
    public List<Event> findAll();

    public void save(Event event);

    public Integer countAllByName(String name);

    public List<Event> findAllByLocation(String location);

    public List<Event> findAllByLocationAndDateStart(String location, String date);
}
