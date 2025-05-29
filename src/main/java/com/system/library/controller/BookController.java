package com.system.library.controller;

import com.system.library.model.*;
import com.system.library.repository.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class BookController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowRequestRepository borrowRequestRepository;

    @Autowired
    private BorrowedBookRepository borrowedBookRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/categorylist")
    public String categorylist(Model model) {
        model.addAttribute("pageTitle", "Book Category - Library");
        model.addAttribute("categories", categoryRepository.findAll());
        return "categorylist";
    }

    @GetMapping("/categoryCreate")
    public String categoryCreate(Model model) {
        model.addAttribute("pageTitle", "Create Category - Library");
        model.addAttribute("categoryForm", new Category());
        model.addAttribute("formMode", "create");
        return "categoryCreate";
    }

    @GetMapping("/category/edit/{id}")
    public String editCategory(@PathVariable("id") Integer id, Model model) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ID"));
        model.addAttribute("pageTitle", "Edit Category - Library");
        model.addAttribute("categoryForm", category);
        model.addAttribute("formMode", "edit");
        return "categoryCreate";
    }

    @PostMapping("/category/save")
    public String saveCategory(@ModelAttribute("categoryForm") @Valid Category form,
                               BindingResult result, Model model) {
        if (form.getCategoryName() == null || form.getCategoryName().trim().isEmpty()) {
            result.rejectValue("name", "error.name", "Category name is required");
            model.addAttribute("pageTitle", "Fix Errors");
            model.addAttribute("formMode", (form.getCategoryId() == null) ? "create" : "edit");
            return "categoryCreate";
        }
        categoryRepository.save(form);
        return "redirect:/categorylist";
    }

    @PostMapping("/category/delete/{id}")
    public String deleteCategory(@PathVariable("id") Integer id) {
        categoryRepository.deleteById(id);
        return "redirect:/categorylist";
    }

    @GetMapping("/book")
    public String book(Model model) {
        model.addAttribute("pageTitle", "Book - Library");

        // Fetch all books from the repository
        List<Book> books = bookRepository.findAll();

        // Add the list of books to the model
        model.addAttribute("books", books);

        return "book";  // The Thymeleaf template to render
    }

    @GetMapping("/book/create")
    public String createBookForm(Model model) {
        model.addAttribute("pageTitle", "Create Book - Library");
        model.addAttribute("bookForm", new Book());
        model.addAttribute("categories", categoryRepository.findAll());
        return "bookCreate";
    }

    @PostMapping("/book/save")
    public String saveBook(@ModelAttribute("bookForm") @Valid Book book,
                           BindingResult result,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "bookCreate";
        }

        if (book.getAvailableCopies() == null) {
            book.setAvailableCopies(book.getTotalCopies());
        }

        bookRepository.save(book);
        return "redirect:/book";
    }

    @PostMapping("/borrow")
    public String borrowBook(@RequestParam("bookId") Integer bookId, RedirectAttributes redirectAttributes, HttpSession session) {
        // Check if user is logged in and has correct role
        Object role = session.getAttribute("role");
        String userEmail = (String) session.getAttribute("userEmail");
        if (role == null || userEmail == null) {
            redirectAttributes.addFlashAttribute("message", "Please log in to borrow a book.");
            return "redirect:/book";
        }

        if (role.equals("Admin")) {
            redirectAttributes.addFlashAttribute("message", "You are an Admin.");
            return "redirect:/book";
        }

        if (!role.equals("user")) {
            redirectAttributes.addFlashAttribute("message", "Only users can borrow books.");
            return "redirect:/book";
        }

        // Get user by email
        User user = userRepository.findByEmail(userEmail)
                .orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "User not found.");
            return "redirect:/book";
        }

        // Check user status
        if (user.getStatus() == 0) {
            redirectAttributes.addFlashAttribute("message", "Your account is not activated.");
            return "redirect:/book";
        }

        // Check borrowing limit
        List<BorrowedBook> borrowedBooks = borrowedBookRepository.findByUserUserIdAndIsReturnedFalse(user.getUserId());
        if (borrowedBooks.size() >= 3) {
            redirectAttributes.addFlashAttribute("message", "You have already borrowed 3 books. Return a book to borrow a new one.");
            return "redirect:/book";
        }

        // Check book availability
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null || book.getAvailableCopies() <= 0) {
            redirectAttributes.addFlashAttribute("message", "The book is out of stock.");
            return "redirect:/book";
        }

        // Check if user has already borrowed this book
        BorrowedBook existingBorrowedBook = borrowedBookRepository.findByUserUserIdAndBookBookIdAndIsReturnedFalse(user.getUserId(), bookId);
        if (existingBorrowedBook != null) {
            redirectAttributes.addFlashAttribute("message", "You have already borrowed this book.");
            return "redirect:/book";
        }

        // Create borrow request
        BorrowRequest borrowRequest = new BorrowRequest();
        borrowRequest.setUser(user);
        borrowRequest.setBook(book);
        borrowRequest.setRequestDate(LocalDate.now());
        borrowRequest.setStatus("Pending");

        borrowRequestRepository.save(borrowRequest);

        redirectAttributes.addFlashAttribute("message", "Borrow request sent to admin.");
        return "redirect:/book";
    }
}
