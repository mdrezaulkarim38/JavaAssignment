package com.system.library.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("loggedInUser")
    public Object getLoggedInUser(HttpSession session) {
        return session.getAttribute("loggedInUser");
    }

    @ModelAttribute("role")
    public String getRole(HttpSession session) {
        Object role = session.getAttribute("role");
        return role != null ? role.toString() : null;
    }
}
