package com.sorinbratosin.EventReminder.Service;

import com.sorinbratosin.EventReminder.DAO.Event;
import com.sorinbratosin.EventReminder.DAO.EventDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {

    @Autowired
    EventDAO eventDAO;

    public List<Event> findAll() {
        return eventDAO.findAll();
    }

    public Integer countAllByName(String name) {
        return eventDAO.countAllByName(name);
    }

    public void save(Event event) {
        eventDAO.save(event);
    }

    public List<Event> eventsByKeyWord(String keyword) {
        List<Event> eventsByKeyWord = new ArrayList<>();
        List<Event> allEvents = eventDAO.findAll();

        for(Event event : allEvents) {
            String[] keywords = event.getName().split("-|:|\\s+");
            for(String s : keywords) {
                if(s.toLowerCase().equals(keyword.toLowerCase())) {
                    eventsByKeyWord.add(event);
                }
            }
        }
        return eventsByKeyWord;
    }
}
