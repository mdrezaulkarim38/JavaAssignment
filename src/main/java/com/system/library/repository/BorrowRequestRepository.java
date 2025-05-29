package com.system.library.repository;

import com.system.library.model.BorrowRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowRequestRepository extends JpaRepository<BorrowRequest, Integer> {
    BorrowRequest findByUserUserIdAndBookBookIdAndStatus(Integer userId, Integer bookId, String status);
}