package com.system.library.controller;

import com.system.library.model.User;
import com.system.library.model.view.RegisterViewModel;
import com.system.library.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("pageTitle", "Register - Library");
        model.addAttribute("registerForm", new RegisterViewModel());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("registerForm") @Valid RegisterViewModel form,
                                  BindingResult result,
                                  Model model) {
        model.addAttribute("pageTitle", "Register - Library");

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
        }

        if (userRepository.existsByEmail(form.getEmail())) {
            result.rejectValue("email", "error.email", "Email already exists");
        }

        if (result.hasErrors()) {
            return "register";
        }

        // Save new user
        User user = new User();
        user.setFullName(form.getFullName());
        user.setEmail(form.getEmail());
        user.setPassword(form.getPassword()); // plain password
        user.setPhone(form.getPhone());
        user.setNidNumber(form.getNidNumber());
        user.setRole("Member");
        user.setMembershipEndDate(null);
        user.setStatus(0);

        userRepository.save(user);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageTitle", "Login - Library");
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {

        // Admin Login Check (hardcoded)
        if (email.equals("admin@example.com") && password.equals("admin123")) {
            session.setAttribute("userEmail", "admin@example.com");
            session.setAttribute("loggedInUser", "admin");
            session.setAttribute("role", "admin");
            return "redirect:/admin";
        }

        // Normal user login
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) {
                session.setAttribute("userEmail", user.getEmail());
                session.setAttribute("loggedInUser", user);
                session.setAttribute("role", "user");
                return "redirect:/user";
            }
        }

        model.addAttribute("error", "Invalid email or password");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
