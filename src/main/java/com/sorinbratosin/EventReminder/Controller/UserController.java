package com.sorinbratosin.EventReminder.Controller;
import com.sorinbratosin.EventReminder.Service.CredentialsAreNotValidException;
import com.sorinbratosin.EventReminder.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    @Autowired
    UserService userService;


    @GetMapping("/register-form")
    public ModelAndView registerAction(@RequestParam("email") String email, @RequestParam("password") String firstPassword, @RequestParam("rewritePassword") String secondPassword) {

        ModelAndView modelAndView = new ModelAndView("register");

        try {
            userService.register(email, firstPassword, secondPassword);
        } catch (Exception ex) {
            modelAndView.addObject("message", ex.getMessage());
            return modelAndView;
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

        try {
            userService.login(email, password);
            modelAndView = new ModelAndView("redirect:/dashboard");
        } catch (CredentialsAreNotValidException credentialsAreNotValidException) {
            modelAndView.addObject("message", credentialsAreNotValidException.getMessage());
        }

        return modelAndView;
    }
}
