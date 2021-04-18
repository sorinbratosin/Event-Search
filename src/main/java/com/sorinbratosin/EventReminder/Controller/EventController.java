package com.sorinbratosin.EventReminder.Controller;


import com.sorinbratosin.EventReminder.DAO.Event;
import com.sorinbratosin.EventReminder.Service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class EventController {

    @Autowired
    EventService eventService;


    @GetMapping("/events")
    @ResponseBody
    public List<Event> eventList() {
        return eventService.findAll();
    }
}
