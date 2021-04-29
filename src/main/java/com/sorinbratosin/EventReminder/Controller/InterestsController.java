package com.sorinbratosin.EventReminder.Controller;

import com.sorinbratosin.EventReminder.DAO.Keyword;
import com.sorinbratosin.EventReminder.Security.UserSession;
import com.sorinbratosin.EventReminder.Service.KeyWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class InterestsController {

    @Autowired
    KeyWordService keyWordService;

    @Autowired
    UserSession userSession;

    @GetMapping("/dashboard")
    public ModelAndView saveNewKeyword() {

        if(userSession.getUserId() == 0) {
            return new ModelAndView("redirect:index.html");
        }

        ModelAndView modelAndView = new ModelAndView("dashboard");
        Iterable<Keyword> keywordList = keyWordService.findAll();

        modelAndView.addObject("keyWords", keywordList);

        return modelAndView;
    }

    @PostMapping("/keywords")
    public ModelAndView saveNewKeyword(@RequestParam("keyword") String keyword) {
        ModelAndView modelAndView = new ModelAndView("dashboard");

        Keyword keywordObj = new Keyword();
        keywordObj.setKeyWord(keyword);

        keyWordService.save(keywordObj);

        Iterable<Keyword> keywordList = keyWordService.findAll();
        modelAndView.addObject("keyWords", keywordList);

        return modelAndView;
    }
}
