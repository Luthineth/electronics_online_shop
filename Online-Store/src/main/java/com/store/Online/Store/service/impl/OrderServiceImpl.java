package com.store.Online.Store.service.impl;

import com.store.Online.Store.dto.OrderItemRequest;
import com.store.Online.Store.entity.Order;
import com.store.Online.Store.entity.OrderItem;
import com.store.Online.Store.entity.Product;
import com.store.Online.Store.entity.User;
import com.store.Online.Store.exception.InvalidOrderQuantityException;
import com.store.Online.Store.exception.OrderCreationException;
import com.store.Online.Store.exception.UserNotFoundException;
import com.store.Online.Store.exception.ProductNotFoundException;
import com.store.Online.Store.repository.orderItemRepository;
import com.store.Online.Store.repository.orderRepository;
import com.store.Online.Store.repository.productRepository;
import com.store.Online.Store.repository.userRepository;
import com.store.Online.Store.service.orderService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements orderService {

    private final orderRepository orderRepository;

    private final productRepository productRepository;

    private final userRepository userRepository;

    private final orderItemRepository orderItemRepository;
    private final EmailServiceImpl emailService;

    public OrderServiceImpl(orderRepository orderRepository, productRepository productRepository,
                            userRepository userRepository, orderItemRepository orderItemRepository,
                            EmailServiceImpl emailService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.emailService = emailService;
    }

    @Transactional
    @Override
    public void createOrder(List<OrderItemRequest> orderItems) {
        Order order = createOrderEntity();
        User user = getUser();
        order.setUserId(user);

        BigDecimal totalPrice = processOrderItems(order, orderItems);

        saveOrderAndSendEmail(order, user, totalPrice);
    }

    private BigDecimal processOrderItems(Order order, List<OrderItemRequest> orderItems) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        int itemNumber = 1;
        StringBuilder descriptionBuilder = new StringBuilder();

        for (OrderItemRequest orderItemRequest : orderItems) {
            Product product = getProductById(orderItemRequest.getProductId());

            OrderItem orderItem = createOrderItem(order, product, orderItemRequest);

            descriptionBuilder.append(formatOrderItemDetails(itemNumber, product, orderItemRequest));
            itemNumber++;

            BigDecimal productTotalPrice = product.getPriceWithDiscount().multiply(BigDecimal.valueOf(orderItemRequest.getQuantity()));
            totalPrice = totalPrice.add(productTotalPrice);
            orderItemRepository.save(orderItem);

            updateProductStock(product, orderItemRequest.getQuantity());
        }

        order.setTotalPrice(totalPrice);
        return totalPrice;
    }

    private OrderItem createOrderItem(Order order, Product product, OrderItemRequest orderItemRequest) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(product);
        orderItem.setQuantity(orderItemRequest.getQuantity());
        orderItem.setOrderId(order);
        return orderItem;
    }

    private String formatOrderItemDetails(int itemNumber, Product product, OrderItemRequest orderItemRequest) {
        StringBuilder details = new StringBuilder();
        details.append(itemNumber).append(". ").append(product.getProductName()).append("\n");
        details.append("   - Количество: ").append(orderItemRequest.getQuantity()).append("\n");
        details.append("   - Стоимость: ").append(product.getPriceWithDiscount()).append(" ₽").append("\n\n");
        return details.toString();
    }

    private void updateProductStock(Product product, int orderedQuantity) {
        int newStockQuantity = product.getStockQuantity() - orderedQuantity;
        if (newStockQuantity < 0) {
            throw new InvalidOrderQuantityException("Invalid order quantity for product with ID: " + product.getProductId());
        }
        product.setStockQuantity(newStockQuantity);
        productRepository.updateStockQuantity(product.getProductId(), newStockQuantity);
    }

    private void saveOrderAndSendEmail(Order order, User user, BigDecimal totalPrice) {
        try {
            orderRepository.save(order);
        } catch (Exception e) {
            throw new OrderCreationException("Failed to create the order. Please try again later.");
        }

        sendOrderConfirmationEmail(user, order, totalPrice);
    }

    private void sendOrderConfirmationEmail(User user, Order order, BigDecimal totalPrice) {
        StringBuilder emailBody = createOrderConfirmationEmailBody(user, order, totalPrice);
        emailService.sendEmail(user.getEmail(), "Ваш заказ и его стоимость", emailBody.toString());
    }

    private StringBuilder createOrderConfirmationEmailBody(User user, Order order, BigDecimal totalPrice) {
        StringBuilder emailBody = new StringBuilder();
        emailBody.append("Спасибо за ваш заказ! Ваш заказ (номер ").append(order.getOrderId()).append(") был успешно обработан. Ниже приведен список товаров в вашем заказе с указанием количества и стоимости каждого товара:\n\n");
        emailBody.append("Уважаемый(ая) ").append(user.getSecondName()).append(" ").append(user.getFirstName()).append(",\n\n");
        emailBody.append("Итоговая стоимость заказа: ").append(totalPrice).append(" ₽").append("\n\n");
        emailBody.append("Мы рады быть вашими партнерами и готовы помочь вам в любое время. Если у вас есть какие-то вопросы или требуется дополнительная информация, пожалуйста, не стесняйтесь связаться с нами.\n\n");
        emailBody.append("С уважением,\nКоманда Наш онлайн магазинчик");

        return emailBody;
    }

    private Order createOrderEntity() {
        Order order = new Order();
        order.setOrderDate(new Date());
        return order;
    }

    private User getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email " + userEmail));
    }

    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
    }
}