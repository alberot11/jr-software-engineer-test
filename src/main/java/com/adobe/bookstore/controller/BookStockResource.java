package com.adobe.bookstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adobe.bookstore.model.BookStock;
import com.adobe.bookstore.repository.BookStockRepository;

@RestController
@RequestMapping("/books_stock/")
public class BookStockResource {

	@Autowired
    private BookStockRepository bookStockRepository;

    @GetMapping("{bookId}")
    public ResponseEntity<BookStock> getStockById(@PathVariable String bookId) {
        return bookStockRepository.findById(bookId)
                .map(bookStock -> ResponseEntity.ok(bookStock))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("all")
    public ResponseEntity<Iterable<BookStock>> getAllBooks() {
        Iterable<BookStock> allBooks = bookStockRepository.findAll();
        return ResponseEntity.ok(allBooks);
    }
}
