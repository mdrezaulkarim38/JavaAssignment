package com.system.library.controller;

import com.system.library.model.Book;
import com.system.library.model.BorrowRequest;
import com.system.library.model.BorrowedBook;
import com.system.library.model.User;
import com.system.library.repository.BookRepository;
import com.system.library.repository.BorrowRequestRepository;
import com.system.library.repository.BorrowedBookRepository;
import com.system.library.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BorrowRequestRepository borrowRequestRepository;

    @Autowired
    private BorrowedBookRepository borrowedBookRepository;

    @GetMapping("/admin")
    public String admin(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Object role = session.getAttribute("role");
        String userEmail = (String) session.getAttribute("userEmail");

        if (role == null || userEmail == null) {
            redirectAttributes.addFlashAttribute("message", "Please log in as an admin.");
            return "redirect:/login";
        }

        if (!role.equals("admin")) {
            redirectAttributes.addFlashAttribute("message", "Access denied. Admin role required.");
            return "redirect:/book";
        }

        List<Book> books = bookRepository.findAll();
        List<User> users = userRepository.findAll();
        List<BorrowRequest> borrowRequests = borrowRequestRepository.findAll();
        List<BorrowedBook> returnRequests = borrowedBookRepository.findAll()
                .stream()
                .filter(b -> b.isUserRequestReturn() && !b.isReturned())
                .toList();

        model.addAttribute("books", books);
        model.addAttribute("users", users);
        model.addAttribute("borrowRequests", borrowRequests);
        model.addAttribute("returnRequests", returnRequests);
        model.addAttribute("pageTitle", "Admin - Library");

        return "admin";
    }

    @PostMapping("/admin/activate-user/{id}")
    public String activateUser(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        userRepository.findById(id).ifPresent(user -> {
            user.setStatus(1);
            userRepository.save(user);
        });
        redirectAttributes.addFlashAttribute("message", "User activated successfully.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/deactivate-user/{id}")
    public String deactivateUser(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        userRepository.findById(id).ifPresent(user -> {
            user.setStatus(0);
            userRepository.save(user);
        });
        redirectAttributes.addFlashAttribute("message", "User deactivated successfully.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/approve-borrow-request")
    public String approveBorrowRequest(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        borrowRequestRepository.findById(id).ifPresent(request -> {
            request.setStatus("Approved");
            borrowRequestRepository.save(request);
        });
        redirectAttributes.addFlashAttribute("message", "Borrow request approved.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/reject-borrow-request")
    public String rejectBorrowRequest(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        borrowRequestRepository.findById(id).ifPresent(request -> {
            request.setStatus("Rejected");
            borrowRequestRepository.save(request);
        });
        redirectAttributes.addFlashAttribute("message", "Borrow request rejected.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/approve-return-book")
    public String approveReturnBook(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        borrowedBookRepository.findById(id).ifPresent(record -> {
            record.setReturned(true);
            record.setUserRequestReturn(false);
            borrowedBookRepository.save(record);
        });
        redirectAttributes.addFlashAttribute("message", "Return request approved.");
        return "redirect:/admin";
    }

    @PostMapping("/admin/reject-return-book")
    public String rejectReturnBook(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        borrowedBookRepository.findById(id).ifPresent(record -> {
            record.setUserRequestReturn(false);
            borrowedBookRepository.save(record);
        });
        redirectAttributes.addFlashAttribute("message", "Return request rejected.");
        return "redirect:/admin";
    }

}