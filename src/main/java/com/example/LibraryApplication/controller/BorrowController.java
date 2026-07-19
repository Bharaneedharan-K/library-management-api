package com.example.LibraryApplication.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.LibraryApplication.model.BookEntity;
import com.example.LibraryApplication.model.BorrowRecord;
import com.example.LibraryApplication.model.UserEntity;
import com.example.LibraryApplication.repository.BookRepository;
import com.example.LibraryApplication.repository.BorrowRepository;
import com.example.LibraryApplication.repository.UserRepository;

@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    // User Endpoint: Borrow a Book
    @PostMapping("/{bookId}")
    public ResponseEntity<String> borrowBook(@PathVariable Long bookId, Authentication authentication) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.isAvailable()) {
            return ResponseEntity.badRequest().body("This book is currently borrowed by another user.");
        }

        UserEntity user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User context not found"));

        book.setAvailable(false);
        bookRepository.save(book);

        BorrowRecord record = new BorrowRecord();
        record.setUser(user);
        record.setBook(book);
        record.setBorrowDate(LocalDate.now());
        borrowRepository.save(record);

        return ResponseEntity.ok("Book borrowed successfully!");
    }

    // User Endpoint: Return a Book
    @PostMapping("/return/{bookId}")
    public ResponseEntity<String> returnBook(@PathVariable Long bookId) {
        BorrowRecord record = borrowRepository.findByBookIdAndReturnDateIsNull(bookId)
                .orElseThrow(() -> new RuntimeException("No active borrow record found for this book."));

        record.setReturnDate(LocalDate.now());
        borrowRepository.save(record);

        BookEntity book = record.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        return ResponseEntity.ok("Book returned successfully!");
    }

    // User Endpoint: Get Personal Active/Past Borrow List
    @GetMapping("/my-list")
    public ResponseEntity<List<BorrowRecord>> getMyBorrowList(Authentication authentication) {
        UserEntity user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(borrowRepository.findByUser(user));
    }
   
}