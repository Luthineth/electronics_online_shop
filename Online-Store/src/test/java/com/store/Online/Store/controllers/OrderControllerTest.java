package com.store.Online.Store.controllers;


import com.store.Online.Store.dto.OrderItemRequest;
import com.store.Online.Store.exception.InvalidOrderQuantityException;
import com.store.Online.Store.exception.OrderCreationException;
import com.store.Online.Store.exception.ProductNotFoundException;
import com.store.Online.Store.service.orderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private orderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder_Successful_ReturnsHttpStatusCreated() {
        List<OrderItemRequest> orderItems = new ArrayList<>();
        doNothing().when(orderService).createOrder(orderItems);
        ResponseEntity<?> response = orderController.createOrder(orderItems);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(orderService, times(1)).createOrder(orderItems);
    }

    @Test
    void testCreateOrder_ProductNotFoundException_ReturnsHttpStatusBadRequest() {
        List<OrderItemRequest> orderItems = new ArrayList<>();
        doThrow(new ProductNotFoundException("Product not found")).when(orderService).createOrder(orderItems);
        ResponseEntity<?> response = orderController.createOrder(orderItems);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Product not found", response.getBody());
        verify(orderService, times(1)).createOrder(orderItems);
    }

    @Test
    void testCreateOrder_InvalidOrderQuantityException_ReturnsHttpStatusBadRequest() {
        List<OrderItemRequest> orderItems = new ArrayList<>();
        doThrow(new InvalidOrderQuantityException("Invalid order quantity for product")).when(orderService).createOrder(orderItems);
        ResponseEntity<?> response = orderController.createOrder(orderItems);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid order quantity for product", response.getBody());
        verify(orderService, times(1)).createOrder(orderItems);
    }
    @Test
    void testCreateOrder_OrderCreationException_ReturnsHttpStatusInternalServerError() {
        List<OrderItemRequest> orderItems = new ArrayList<>();
        doThrow(new OrderCreationException("Error creating order")).when(orderService).createOrder(orderItems);
        ResponseEntity<?> response = orderController.createOrder(orderItems);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error creating order", response.getBody());
        verify(orderService, times(1)).createOrder(orderItems);
    }
}