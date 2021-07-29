package com.sorinbratosin.EventReminder.Service;

import com.sorinbratosin.EventReminder.DAO.Event;
import com.sorinbratosin.EventReminder.DAO.EventDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public Set<Event> eventsByKeyWord(String keyword) {
        Set<Event> eventsByKeyWord = new HashSet<>();
        List<Event> allEvents = eventDAO.findAll();

        for(Event event : allEvents) {
            String[] keywordsFromTitle = event.getName().split("-|\\.|:|\\s+");
            for(String s : keywordsFromTitle) {
                s = removeDiacriticsFromString(s);
                if(s.toLowerCase().equals(keyword.toLowerCase())) {
                    eventsByKeyWord.add(event);
                }
            }

            String[] keywordsFromLocation = event.getLocation().split("-|\\.|:|\\s+");
            for(String s : keywordsFromLocation) {
                s = removeDiacriticsFromString(s);
                if(s.toLowerCase().equals(keyword.toLowerCase())) {
                    eventsByKeyWord.add(event);
                }
            }

            String[] keywordsFromCity = event.getCity().split("-|\\.|:|\\s+");
            for(String s : keywordsFromCity) {
                s = removeDiacriticsFromString(s);
                if(s.toLowerCase().equals(keyword.toLowerCase())) {
                    eventsByKeyWord.add(event);
                }
            }

            String[] keywordsFromGenre = event.getGenre().split("-|\\.|:|\\s+");
            for(String s : keywordsFromGenre) {
                s = removeDiacriticsFromString(s);
                if(s.toLowerCase().equals(keyword.toLowerCase())) {
                    eventsByKeyWord.add(event);
                }
            }
    }
        return eventsByKeyWord;
    }

    private String removeDiacriticsFromString(String string) {
        string = Normalizer.normalize(string, Normalizer.Form.NFD);
        string = string.replaceAll("[^\\p{ASCII}]", "");
        return string;
    }
}
