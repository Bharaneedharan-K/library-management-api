package com.example.LibraryApplication.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.LibraryApplication.model.BorrowRecord;
import com.example.LibraryApplication.model.UserEntity;

public interface BorrowRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByUser(UserEntity user);
    Optional<BorrowRecord> findByBookIdAndReturnDateIsNull(Long bookId);
}