package com.adobe.bookstore.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public class OrderRequestDTO {
	
	@NotEmpty(message = "bookIds cannot be empty")
	private List<String> bookIds;
	
	@NotEmpty(message = "quantities cannot be empty")
    private List<Integer> quantities;
    
	public List<String> getBookIds() {
		return bookIds;
	}
	
	public void setBookIds(List<String> bookIds) {
		this.bookIds = bookIds;
	}
	
	public List<Integer> getQuantities() {
		return quantities;
	}
	
	public void setQuantities(List<Integer> quantities) {
		this.quantities = quantities;
	}
	
	public boolean isConsistent() {
        	return bookIds.size() == quantities.size();
    	}
}
