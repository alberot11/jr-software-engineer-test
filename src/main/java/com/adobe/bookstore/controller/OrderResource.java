package com.adobe.bookstore.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adobe.bookstore.dto.OrderRequestDTO;
import com.adobe.bookstore.model.BookOrder;
import com.adobe.bookstore.repository.OrderRepository;
import com.adobe.bookstore.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderResource {

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderRequestDTO orderRequest) {
		
		if (!orderRequest.isConsistent()) {
			return ResponseEntity.badRequest().body("BookIds and quantities must have the same size");
		}
		
		List<String> bookIds = orderRequest.getBookIds();

		List<Integer> quantities = orderRequest.getQuantities();

        try {
            String orderId = orderService.createOrder(bookIds, quantities);
            return ResponseEntity.ok("Order created successfully with ID " + orderId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
	
	@GetMapping
	public ResponseEntity<List<BookOrder>> extractOrders() {
	    List<BookOrder> orders = StreamSupport
	                            .stream(orderRepository.findAll().spliterator(), false)
	                            .collect(Collectors.toList());

	    if (orders.isEmpty()) {
	        return ResponseEntity.noContent().build();
	    }

	    return ResponseEntity.ok(orders);
	}
	
}
