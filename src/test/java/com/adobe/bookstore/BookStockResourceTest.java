package com.adobe.bookstore;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import com.adobe.bookstore.model.BookOrder;
import com.adobe.bookstore.model.BookStock;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
class BookStockResourceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @AfterEach
    public void cleanDatabase() {
        restTemplate.delete("http://localhost:" + port + "/orders");
        restTemplate.delete("http://localhost:" + port + "/books_stock/all");
        restTemplate.delete("http://localhost:" + port + "/order_items");
    }

    @Test
    @Order(1)
    @Sql(statements = "INSERT INTO book_stock (id, name, quantity) VALUES ('12345-67890', 'some book', 7)")
    public void shouldReturnCurrentStock() {
        var result = restTemplate.getForObject(
                "http://localhost:" + port + "/books_stock/12345-67890", 
                BookStock.class
        );

        assertThat(result).isNotNull();
        assertThat(result.getQuantity()).isEqualTo(7);
    }
    
    @Test
    @Order(2)
    @Sql(statements = {
            "INSERT INTO book_stock (id, name, quantity) VALUES ('book-123', 'Book One', 10)",
            "INSERT INTO book_stock (id, name, quantity) VALUES ('book-456', 'Book Two', 5)"
    })
    public void shouldCreateOrderSuccessfully() {
        var orderRequest = Map.of(
                "bookIds", List.of("book-123"),
                "quantities", List.of(2)
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/orders",
                orderRequest,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Order created successfully with ID");
    }

    @Test
    @Order(3)
    @Sql(statements = {
            "INSERT INTO book_stock (id, name, quantity) VALUES ('book-789', 'Book Three', 1)"
    })
    public void shouldReturnBadRequestWhenNotEnoughStock() {
        var orderRequest = Map.of(
                "bookIds", List.of("book-789"),
                "quantities", List.of(5)
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/orders",
                orderRequest,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Not enough stock for book ID");
    }

    @Test
    @Order(4)
    public void shouldReturnBadRequestWhenInconsistentLists() {
        var orderRequest = Map.of(
                "bookIds", List.of("book-123"),
                "quantities", List.of(2, 1)
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/orders",
                orderRequest,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("BookIds and quantities must have the same size");
    }

    @Test
    @Order(5)
    @Sql(statements = {
            "INSERT INTO book_stock (id, name, quantity) VALUES ('book-000', 'Book Zero', 5)",
            "INSERT INTO orders (id, status) VALUES (2, 'SUCCESS')",
            "INSERT INTO order_items (order_id, book_id, quantity) VALUES (2, 'book-000', 2)"
    })
    public void shouldReturnOrders() {
        ResponseEntity<BookOrder[]> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/orders",
                BookOrder[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }


}
