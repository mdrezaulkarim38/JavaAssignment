package com.system.library.controller;

import com.system.library.model.BorrowedBook;
import com.system.library.model.BorrowRequest;
import com.system.library.model.User;
import com.system.library.repository.BorrowedBookRepository;
import com.system.library.repository.BorrowRequestRepository;
import com.system.library.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private BorrowedBookRepository borrowedBookRepository;

    @Autowired
    private BorrowRequestRepository borrowRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user")
    public String user(Model model, HttpSession session) {
        String email = (String) session.getAttribute("userEmail");

        if (email == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return "redirect:/login";

        List<BorrowedBook> borrowedBooks = borrowedBookRepository.findByUserUserIdAndIsReturnedFalse(user.getUserId());
        List<BorrowRequest> borrowRequests = borrowRequestRepository.findAll().stream()
                .filter(r -> r.getUser().getUserId().equals(user.getUserId()))
                .toList();

        model.addAttribute("borrowedBooks", borrowedBooks);
        model.addAttribute("borrowRequests", borrowRequests);
        model.addAttribute("pageTitle", "User - Library");

        return "user";
    }

    @PostMapping("/user/request-return")
    public String requestReturn(@RequestParam("borrowedBookId") Integer id, HttpSession session) {
        borrowedBookRepository.findById(id).ifPresent(b -> {
            b.setUserRequestReturn(true);
            borrowedBookRepository.save(b);
        });
        return "redirect:/user";
    }

    @PostMapping("/user/cancel-request")
    public String cancelRequest(@RequestParam("borrowRequestId") Integer id) {
        borrowRequestRepository.deleteById(id);
        return "redirect:/user";
    }
}
