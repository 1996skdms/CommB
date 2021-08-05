package com.ssafy.commb.repository;

import com.ssafy.commb.model.Book;
import com.ssafy.commb.model.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Integer> {

    public Optional<Book> findByIsbn(String isbn);
}
