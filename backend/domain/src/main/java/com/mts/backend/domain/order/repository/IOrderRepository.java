package com.mts.backend.domain.order.repository;

import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.order.Order;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.domain.promotion.identifier.DiscountId;

import java.util.List;
import java.util.Optional;

public interface IOrderRepository {
    
    Optional<Order> findById(OrderId id);
    
    Order save(Order order);
    
    List<Order> findAll();
    
    void delete(Order order);
    
    List<Order> findByCustomerId(CustomerId customerId);
    
    List<Order> findByCustomerIdAndStatus(CustomerId customerId, OrderStatus status);
    
    Optional<Order> findByCustomerIdAndOrderId(CustomerId customerId, OrderId orderId);
    
    Long countOrderAndApplyDiscount(CustomerId customerId, DiscountId discountId);
    
}
