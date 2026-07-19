package com.example.LibraryApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.LibraryApplication.model.BookEntity;

public interface BookRepository extends JpaRepository<BookEntity, Long> {
}