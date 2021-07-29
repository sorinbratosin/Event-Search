package com.sorinbratosin.EventReminder.Controller;

import com.sorinbratosin.EventReminder.DAO.Event;
import com.sorinbratosin.EventReminder.Security.UserSession;
import com.sorinbratosin.EventReminder.Service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class InterestsController {

    @Autowired
    UserSession userSession;

    @Autowired
    EventService eventService;

    @GetMapping("/dashboard")
    public ModelAndView saveNewKeyword() {

        if(userSession.getUserId() == 0) {
            return new ModelAndView("redirect:index.html");
        }

        ModelAndView modelAndView = new ModelAndView("dashboard");
        return modelAndView;
    }

    @GetMapping("/keywords")
    public ModelAndView saveNewKeyword(@RequestParam("keyword") String keyword) {
        ModelAndView modelAndView = new ModelAndView("dashboard");

        Iterable<Event> events = eventService.eventsByKeyWord(keyword);

        modelAndView.addObject("events", events);

        return modelAndView;
    }
}
