package com.store.Online.Store.controllers;


import com.store.Online.Store.dto.OrderItemRequest;
import com.store.Online.Store.exception.InvalidOrderQuantityException;
import com.store.Online.Store.exception.OrderCreationException;
import com.store.Online.Store.exception.ProductNotFoundException;
import com.store.Online.Store.exception.UserNotFoundException;
import com.store.Online.Store.service.orderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private orderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody List<OrderItemRequest> orderItems) {
        try {
            orderService.createOrder(orderItems);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (UserNotFoundException | ProductNotFoundException | InvalidOrderQuantityException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (OrderCreationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}