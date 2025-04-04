package com.mts.backend.domain.order.jpa;

import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.order.OrderEntity;
import com.mts.backend.domain.order.identifier.OrderDiscountId;
import com.mts.backend.domain.order.identifier.OrderId;
import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.domain.promotion.identifier.DiscountId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long> {
    @EntityGraph(attributePaths = {"customerEntity.membershipTypeEntity.memberDiscountValue", "employeeEntity", "orderDiscounts.discount.couponEntity", "orderProducts.productPriceEntity.productEntity.categoryEntity", "orderProducts.productPriceEntity.size", "orderTables.table"})
    @Query("select o from OrderEntity o where o.id = :id")
    Optional<OrderEntity> findOrderWithDetails(@Param("id") @NonNull Long id);

    @Query("""
            select count(o) from OrderEntity o inner join o.orderDiscounts orderDiscounts
            where o.customerEntity.id = :id and orderDiscounts.discount.id = :id1 and o.status = :status""")
    long countByCustomerEntity_IdAndOrderDiscounts_Discount_IdAndStatus(@Param("id") @NonNull Long id,
                                                                        @Param("id1") @NonNull Long id1,
                                                                        @Param("status") @NonNull OrderStatus status);


    @Query("select count(DISTINCT o) from OrderEntity o inner join o.orderDiscounts od " +
            "where o.customerEntity.id = :customerId and o.status = 'COMPLETED' " +
            "and od.discount.id = :discountId")
    long countCompletedOrdersWithSpecificDiscount(
            @Param("customerId") Long customerId,
            @Param("discountId") Long discountId
    );
    


    @Query("select o from OrderEntity o where o.customerEntity.id is not null and o.customerEntity.id = :id")
    List<OrderEntity> findByCustomerEntity_IdNotNullAndCustomerEntity_Id(@Param("id") @NonNull Long id);

    @Query("select o from OrderEntity o where o.employeeEntity.id = :id")
    List<OrderEntity> findByEmployeeEntity_Id(@Param("id") @NonNull CustomerId id);
    
    @EntityGraph(value = "OrderEntity.detail", type = EntityGraph.EntityGraphType.LOAD)
    Optional<OrderEntity> findDetailById(Long id);
}