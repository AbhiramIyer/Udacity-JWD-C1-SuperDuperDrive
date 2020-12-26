package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SignupController {

    private UserService userService;

    public SignupController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String getSignupPage(@ModelAttribute("user") User newUser, Model model) {
        return "signup";
    }

    @PostMapping ("/signup")
    public String processSignup(@ModelAttribute("user") User newUser, Model model) {
        if (!userService.isUsernameAvailable(newUser.getUsername())) {
            model.addAttribute("signupErrorMessage", "Account already exists");
        } else {
            userService.createUser(newUser);
            model.addAttribute("signupSuccessful", "true");
        }
        return "signup";
    }
}
