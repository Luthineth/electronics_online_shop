package com.store.Online.Store.service;

import com.store.Online.Store.dto.OrderItemRequest;
import com.store.Online.Store.entity.Discount;
import com.store.Online.Store.entity.Product;
import com.store.Online.Store.entity.Role;
import com.store.Online.Store.entity.User;
import com.store.Online.Store.exception.InvalidOrderQuantityException;
import com.store.Online.Store.exception.OrderCreationException;
import com.store.Online.Store.exception.ProductNotFoundException;
import com.store.Online.Store.repository.orderItemRepository;
import com.store.Online.Store.repository.orderRepository;
import com.store.Online.Store.repository.productRepository;
import com.store.Online.Store.repository.userRepository;
import com.store.Online.Store.service.impl.EmailServiceImpl;
import com.store.Online.Store.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private orderRepository orderRepository;

    @Mock
    private productRepository productRepository;

    @Mock
    private userRepository userRepository;

    @Mock
    private orderItemRepository orderItemRepository;

    @Mock
    private EmailServiceImpl emailService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createOrder_ProductNotFound_ThrowsProductNotFoundException() {
        User user = new User();
        user.setEmail("test@example.com");

        List<OrderItemRequest> orderItems = new ArrayList<>();
        orderItems.add(new OrderItemRequest(1L, 2));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> orderService.createOrder(orderItems));
        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).save(any());
        verify(productRepository, never()).updateStockQuantity(anyLong(), anyInt());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void createOrder_InvalidOrderQuantity_ThrowsInvalidOrderQuantityException() {
        List<OrderItemRequest> orderItems = new ArrayList<>();
        orderItems.add(new OrderItemRequest(1L, 20));

        Product product = new Product();
        product.setStockQuantity(10);

        User user = new User();
        user.setEmail("test@example.com");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(anyString())).thenReturn(java.util.Optional.of(user));
        when(productRepository.findById(anyLong())).thenReturn(java.util.Optional.of(product));

        assertThrows(InvalidOrderQuantityException.class, () -> orderService.createOrder(orderItems));
        verify(orderRepository, never()).save(any());
        verify(orderItemRepository, never()).save(any());
        verify(productRepository, never()).updateStockQuantity(anyLong(), anyInt());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void createOrder_OrderCreationFailed_ThrowsOrderCreationException() {
        List<OrderItemRequest> orderItems = new ArrayList<>();
        orderItems.add(new OrderItemRequest(1L, 2));

        Product product = new Product();
        product.setProductId(1L);
        product.setStockQuantity(10);
        product.setPriceWithDiscount(BigDecimal.TEN);

        User user = new User();
        user.setEmail("test@example.com");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(anyString())).thenReturn(java.util.Optional.of(user));
        when(productRepository.findById(anyLong())).thenReturn(java.util.Optional.of(product));
        when(orderRepository.save(any())).thenThrow(new RuntimeException("Test exception"));

        assertThrows(OrderCreationException.class, () -> orderService.createOrder(orderItems));
        verify(productRepository, never()).updateStockQuantity(anyLong(), anyInt());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void createOrder_ValidOrderItems_SuccessfulOrderCreation() {
        List<OrderItemRequest> orderItems = new ArrayList<>();
        orderItems.add(new OrderItemRequest(1L, 2)); // Пример данных

        Product product = new Product();
        product.setProductId(1L);
        product.setStockQuantity(10);
        product.setPriceWithDiscount(BigDecimal.TEN);

        User user = new User();
        user.setEmail("test@example.com");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(anyString())).thenReturn(java.util.Optional.of(user));
        when(productRepository.findById(anyLong())).thenReturn(java.util.Optional.of(product));

        orderService.createOrder(orderItems);
        verify(orderRepository, times(1)).save(any());
        verify(orderItemRepository, times(1)).save(any());
        verify(productRepository, times(1)).updateStockQuantity(anyLong(), anyInt());
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }
}
