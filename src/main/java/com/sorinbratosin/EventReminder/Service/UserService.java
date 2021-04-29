package com.sorinbratosin.EventReminder.Service;

import com.sorinbratosin.EventReminder.DAO.User;
import com.sorinbratosin.EventReminder.DAO.UserDAO;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserDAO userDAO;

    public void save (String email, String password) throws PasswordLengthException, EmailAlreadyTakenException {
        if(password.length() < 7) {
            throw new PasswordLengthException("Password should have more than 7 characters");
        } else if (searchUserByEmail(email).size() >= 1) {
            throw new EmailAlreadyTakenException("This e-mail is already associated with an account");
        } else {
            // we use de md5Hex function to "encrypt" the password before storing it in the database
            String passwordMD5 = DigestUtils.md5Hex(password);
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordMD5);
            userDAO.save(user);
        }
    }

    public List<User> searchUserByEmail(String email) {
        return userDAO.searchUserByEmail(email);
    }
}
