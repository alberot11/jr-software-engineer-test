package com.adobe.bookstore.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.adobe.bookstore.model.BookStock;
import com.adobe.bookstore.model.BookOrder;
import com.adobe.bookstore.model.OrderItem;
import com.adobe.bookstore.repository.BookStockRepository;
import com.adobe.bookstore.repository.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService {
	
	private OrderRepository orderRepository;
	
	private BookStockRepository bookStockRepository;
	
	@Autowired
	public OrderServiceImpl (OrderRepository orderRepository, BookStockRepository bookStockRepository) {
		this.orderRepository = orderRepository;
		this.bookStockRepository = bookStockRepository;
	}

	@Override
	public String createOrder(List<String> bookIds, List<Integer> quantities) {
		
		List<OrderItem> orderItems = new ArrayList<>();
		
		for (int i = 0; i < bookIds.size(); i++) {
			BookStock bookStock = bookStockRepository.findById(bookIds.get(i)).orElse(null);
			
			if (bookStock == null) {
				throw new IllegalArgumentException("There is no book with ID " + bookIds.get(i));
			} 
			
			if (bookStock.getQuantity() < quantities.get(i)) {
				throw new IllegalArgumentException("Not enough stock for book ID " + bookIds.get(i));
			}
			
			orderItems.add(new OrderItem(bookStock, quantities.get(i)));
		}
		
		BookOrder order = new BookOrder(orderItems, "SUCCESS");
		orderItems.forEach(item -> item.setOrder(order));
		orderRepository.save(order);
		
		updateStockAsync(bookIds, quantities);
		
		return order.getId().toString();
	}
	
	@Async
	public void updateStockAsync (List<String> bookIds, List<Integer> quantities) {
		for (int i = 0; i < bookIds.size(); i++) {
			BookStock bookStock = bookStockRepository.findById(bookIds.get(i)).orElse(null);
			try {
				int updatedQuantity = bookStock.getQuantity() - quantities.get(i);
				bookStock.setQuantity(updatedQuantity);
				System.out.println("New quantity for book with ID " + bookIds.get(i) + " -> " + updatedQuantity);
				bookStockRepository.save(bookStock);
			} catch (NullPointerException e) {
				System.out.println("There is no book with ID " + bookIds.get(i));
			} catch (Exception e) {
				System.out.println("Error updating stock for book ID " + bookIds.get(i));
				e.printStackTrace();
			}
		}
	}

}
