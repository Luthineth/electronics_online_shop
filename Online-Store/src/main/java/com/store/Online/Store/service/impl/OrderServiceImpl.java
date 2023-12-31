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

        BigDecimal totalPrice = BigDecimal.ZERO;
        int itemNumber = 1;
        StringBuilder descriptionBuilder = new StringBuilder();

        for (OrderItemRequest orderItemRequest : orderItems) {
            Product product = productRepository.findById(orderItemRequest.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + orderItemRequest.getProductId()));

            int newStockQuantity = product.getStockQuantity() - orderItemRequest.getQuantity();
            if (newStockQuantity < 0) {
                throw new InvalidOrderQuantityException("Invalid order quantity for product with ID: " + product.getProductId());
            }


            if (product != null) {
                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(product);
                orderItem.setQuantity(orderItemRequest.getQuantity());
                orderItem.setOrderId(order);

                descriptionBuilder.append(itemNumber).append(". ").append(product.getProductName()).append("\n");
                descriptionBuilder.append("   - Количество: ").append(orderItemRequest.getQuantity()).append("\n");
                descriptionBuilder.append("   - Стоимость: ").append(product.getPriceWithDiscount()).append(" ₽").append("\n\n");
                itemNumber++;

                BigDecimal productTotalPrice = product.getPriceWithDiscount().multiply(BigDecimal.valueOf(orderItemRequest.getQuantity()));
                totalPrice = totalPrice.add(productTotalPrice);
                orderItemRepository.save(orderItem);
            }
        }

        order.setTotalPrice(totalPrice);
        try {
            orderRepository.save(order);
        } catch (Exception e) {
            throw new OrderCreationException("Failed to create the order. Please try again later.");
        }


        for (OrderItemRequest orderItemRequest : orderItems) {
            Product product = productRepository.findById(orderItemRequest.getProductId()).orElse(null);

            if (product != null) {
                int newStockQuantity = product.getStockQuantity() - orderItemRequest.getQuantity();
                product.setStockQuantity(newStockQuantity);
                productRepository.updateStockQuantity(product.getProductId(), newStockQuantity);
            }
        }

        descriptionBuilder.append("Итоговая стоимость заказа: ").append(totalPrice).append(" ₽").append("\n\n");

        descriptionBuilder.append("Мы рады быть вашими партнерами и готовы помочь вам в любое время. Если у вас есть какие-то вопросы или требуется дополнительная информация, пожалуйста, не стесняйтесь связаться с нами.\n\n");
        descriptionBuilder.append("С уважением,\nКоманда ").append("Наш онлайн магазинчик");

        descriptionBuilder.insert(0, "Спасибо за ваш заказ! Ваш заказ (номер " + order.getOrderId() + ") был успешно обработан. Ниже приведен список товаров в вашем заказе с указанием количества и стоимости каждого товара:\n\n");
        descriptionBuilder.insert(0, "Уважаемый(ая) " + user.getSecondName() + " " + user.getFirstName() + ",\n\n");

        String body = descriptionBuilder.toString();

        String subject = "Ваш заказ и его стоимость";

        emailService.sendEmail(user.getEmail(),subject,body);
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
}