package com.adobe.bookstore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "order_id", nullable = false)
	@JsonIgnore
	private BookOrder order;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "book_id", nullable = false)
	private BookStock book;
	
	@Column(name = "quantity", nullable = false)
	private Integer quantity;

	public OrderItem(BookStock book, Integer quantity) {
		this.book = book;
		this.quantity = quantity;
	}

	public OrderItem() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BookOrder getOrder() {
		return order;
	}

	public void setOrder(BookOrder order) {
		this.order = order;
	}

	public BookStock getBook() {
		return book;
	}

	public void setBook(BookStock book) {
		this.book = book;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
		
	
}
