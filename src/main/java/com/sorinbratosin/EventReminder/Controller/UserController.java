package com.sorinbratosin.EventReminder.Controller;

import com.sorinbratosin.EventReminder.DAO.User;
import com.sorinbratosin.EventReminder.Security.UserSession;
import com.sorinbratosin.EventReminder.Service.EmailAlreadyTakenException;
import com.sorinbratosin.EventReminder.Service.PasswordLengthException;
import com.sorinbratosin.EventReminder.Service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class UserController {

    private static final String message = "Credentials are not valid!";

    @Autowired
    UserService userService;

    @Autowired
    UserSession userSession;

    @GetMapping("/register-form")
    public ModelAndView registerAction(@RequestParam("email") String email, @RequestParam("password") String firstPassword, @RequestParam("rewritePassword") String secondPassword) {

        ModelAndView modelAndView = new ModelAndView("register");

        if (!firstPassword.equals(secondPassword)) {
            modelAndView.addObject("message", "Passwords don't match!");
            return modelAndView;
            //if the passwords don't match show the message in a paragraph in register.html using Thymeleaf
        } else {
            //save it to the database
            try {
                userService.save(email, firstPassword);
            } catch (PasswordLengthException | EmailAlreadyTakenException registerException) {
                modelAndView.addObject("message", registerException.getMessage());
                return modelAndView;
            }
        }
        return new ModelAndView("redirect:/index.html");
    }

    @GetMapping("/register")
    public ModelAndView register() {
        return new ModelAndView("register");
    }

    //goes here when in the form(index.html) the submit button is pressed. Gets the parameters email and password from the URL
    @PostMapping("/login")
    public ModelAndView login(@RequestParam("email") String email, @RequestParam("password") String password) {
        ModelAndView modelAndView = new ModelAndView("index");
        List<User> userList = userService.searchUserByEmail(email);

        //if the size of the List is 0 then no email matched, that means the user doesn't exist in the database. If we found more than 1 user with that email, then again it's not ok
        //If we found 1 user with that email check if the password entered and the one from the database match. If it matches return a new VIEW dashboard(dashboard.html)
        if (userList.size() == 0) {
            modelAndView.addObject("message", message);
        } else if (userList.size() > 1) {
            modelAndView.addObject("message", message);
        } else {
            User userFromDatabase = userList.get(0);
            //we "encrypt" the password again because the one from the browser won't match the encrypted one from the database cuz it will return as a string. So compare encrypted to encrypted
            if (!userFromDatabase.getPassword().equals(DigestUtils.md5Hex(password))) {
                modelAndView.addObject("message", message);
            } else {
                userSession.setUserId(userFromDatabase.getId());
                modelAndView = new ModelAndView("redirect:/dashboard");
            }
        }
        return modelAndView;
    }
}
