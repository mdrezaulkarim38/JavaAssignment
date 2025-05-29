package com.system.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Home - Library");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "About - Library");
        return "about";
    }

    @GetMapping("/privacy")
    public String privacy(Model model) {
        model.addAttribute("pageTitle", "Privacy - Library");
        return "privacy";
    }
        
}