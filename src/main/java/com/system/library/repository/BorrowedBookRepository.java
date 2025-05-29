package com.system.library.repository;

import com.system.library.model.BorrowedBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowedBookRepository extends JpaRepository<BorrowedBook, Integer> {
    List<BorrowedBook> findByUserUserIdAndIsReturnedFalse(Integer userId);
    BorrowedBook findByUserUserIdAndBookBookIdAndIsReturnedFalse(Integer userId, Integer bookId);
}