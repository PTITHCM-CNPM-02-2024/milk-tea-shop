package com.mts.backend.domain.order.jpa;

import com.mts.backend.domain.order.OrderProductEntity;
import com.mts.backend.domain.order.identifier.OrderProductId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderProductRepository extends JpaRepository<OrderProductEntity, Long> {

    


}