package com.system.library.model.view;

import com.system.library.model.BorrowedBook;
import com.system.library.model.BorrowRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserBorrowHistoryViewModel {

    private List<BorrowedBook> borrowedBooks;
    private List<BorrowRequest> borrowRequests;
}
