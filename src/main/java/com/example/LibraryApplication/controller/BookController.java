package com.example.LibraryApplication.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.LibraryApplication.model.BookEntity;
import com.example.LibraryApplication.repository.BookRepository;
import com.example.LibraryApplication.service.S3Service;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private S3Service s3Service;

    // Admin Endpoint: Adds book and mocks multipart image uploading (Future S3 hook)
    @PostMapping("/add")
    public ResponseEntity<String> addBook(
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam(value = "coverImage", required = false) MultipartFile file) {
        
        BookEntity book = new BookEntity();
        book.setTitle(title);
        book.setAuthor(author);

        if (file != null && !file.isEmpty()) {
            // Call S3 upload logic
            String uploadedUrl = s3Service.uploadFile(file);
            book.setCoverImgUrl(uploadedUrl);
        } else {
            book.setCoverImgUrl("https://s3.amazonaws.com/library-covers-bucket/default-cover.png");
        }

        bookRepository.save(book);
        return ResponseEntity.ok("Book added to catalog successfully!");
    }

    // Find All Books Endpoint
    @GetMapping("/all")
    public ResponseEntity<List<BookEntity>> getAllBooks() {
        return ResponseEntity.ok(bookRepository.findAll());
    }
}