package com.store.Online.Store.service;

import com.store.Online.Store.dto.OrderItemRequest;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface orderService {

    @Transactional
    void createOrder(List<OrderItemRequest> orderItemRequests);
}
