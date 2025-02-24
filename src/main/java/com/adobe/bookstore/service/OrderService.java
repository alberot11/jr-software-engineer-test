package com.adobe.bookstore.service;

import java.util.List;

public interface OrderService {

	String createOrder(List<String> bookIds, List<Integer> quantities);
	
}
