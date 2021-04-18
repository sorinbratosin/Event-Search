package com.sorinbratosin.EventReminder.Service;

import com.sorinbratosin.EventReminder.DAO.KeyWordDAO;
import com.sorinbratosin.EventReminder.DAO.Keyword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeyWordService {

    @Autowired
    KeyWordDAO keywordDAO;

    public List<Keyword> findAllByKeyWord(String word) {
        return keywordDAO.findAllByKeyword(word);
    }

    public void save(Keyword keyword) {
        keywordDAO.save(keyword);
    }

    public Iterable<Keyword> findAll() {
        return keywordDAO.findAll();
    }
}
