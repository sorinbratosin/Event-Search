package com.sorinbratosin.EventReminder.Service;

import com.sorinbratosin.EventReminder.DAO.User;
import com.sorinbratosin.EventReminder.DAO.UserDAO;
import com.sorinbratosin.EventReminder.Security.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

@Service
public class UserService {

    private static final String message = "Credentials are not valid!";

    @Autowired
    UserDAO userDAO;

    @Autowired
    UserSession userSession;

    public void save(String email, String password) throws PasswordLengthException, EmailAlreadyTakenException {
        if (password.length() <= 7) {
            throw new PasswordLengthException("Password should have more than 7 characters");
        } else if (searchUserByEmail(email).size() >= 1) {
            throw new EmailAlreadyTakenException("This e-mail is already associated with an account");
        } else {

            User user = userPassSaltHash(email, password);
            userDAO.save(user);
        }
    }

    public void login(String email, String password) throws CredentialsAreNotValidException {
        List<User> userList = userDAO.searchUserByEmail(email);

        if (userList.size() == 0) {
            throw new CredentialsAreNotValidException(message);
        } else {
            User user = userList.get(0);

            if (!saltedHashPass(user.getSalt(), password).equals(user.getHashed())) {
                throw new CredentialsAreNotValidException(message);
            } else {
                userSession.setUserId(user.getId());
            }
        }
    }

    public void register(String email, String passwordOne, String passwordTwo) throws PasswordsDontMatchException, PasswordLengthException, EmailAlreadyTakenException {
        if (!passwordOne.equals(passwordTwo)) {
            throw new PasswordsDontMatchException("Passwords don't match!");
        } else {
            save(email, passwordOne);
        }
    }

    private User userPassSaltHash(String email, String password) {
        User user = new User();

        //generate random salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        user.setEmail(email);
        user.setSalt(salt);
        user.setHashed(saltedHashPass(salt, password));

        return user;
    }

    private String saltedHashPass(byte[] salt, String password) {
        StringBuilder hashedPassBuilder = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            //passing the salt to the digest for computation
            md.update(salt);

            //Generate the salted hash
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            for (byte b : hashedPassword) {
                hashedPassBuilder.append(String.format("%02x", b));
            }

        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
        }

        return hashedPassBuilder.toString();
    }


    public List<User> searchUserByEmail(String email) {
        return userDAO.searchUserByEmail(email);
    }
}
