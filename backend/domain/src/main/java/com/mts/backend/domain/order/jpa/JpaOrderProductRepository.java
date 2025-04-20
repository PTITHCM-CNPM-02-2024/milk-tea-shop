package com.mts.backend.domain.order.jpa;

import com.mts.backend.domain.order.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderProductRepository extends JpaRepository<OrderProduct, Long> {

    


}