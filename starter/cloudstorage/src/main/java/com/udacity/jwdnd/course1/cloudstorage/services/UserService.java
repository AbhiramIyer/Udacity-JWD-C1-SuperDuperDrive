package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private Map<String, User> accounts;

    public int createUser(User newUser) {
        if (!accounts.containsKey(newUser.getUsername())) {
            accounts.put(newUser.getUsername(), newUser);
            return accounts.size();
        }
        return -1;
    }

    public User getUser(String username) {
        return accounts.get(username);
    }

    @PostConstruct
    private void init() {
        accounts = new HashMap<>();
    }
}
