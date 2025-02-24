package com.adobe.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adobe.bookstore.model.BookOrder;

public interface OrderRepository extends JpaRepository<BookOrder, Long> {

}
