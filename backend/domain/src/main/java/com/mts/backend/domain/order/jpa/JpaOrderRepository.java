package com.mts.backend.domain.order.jpa;

import com.mts.backend.domain.persistence.entity.OrderEntity;
import com.mts.backend.domain.promotion.identifier.CouponId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long> {
    @Query("select count(DISTINCT o) from OrderEntity o inner join o.orderDiscounts od " +
            "where o.customerEntity.id = :customerId and o.status = 'COMPLETED' " +
            "and od.discount.id = :discountId")
    long countCompletedOrdersWithSpecificDiscount(
            @Param("customerId") CouponId customerId,
            @Param("discountId") Long discountId
    );

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO milk_tea_shop_prod.Order (order_id, customer_id, employee_id, " +
            "status, total_amount, final_amount, order_time, customize_note) " +
            "VALUES (:#{#entity.id}, :#{#entity.customerEntity?.id}, :#{#entity.employeeEntity.id}, " +
            ":#{#entity.status?.name()}, :#{#entity.totalAmount}, :#{#entity.finalAmount}, " +
            ":#{#entity.orderTime}, :#{#entity.customizeNote})",
            nativeQuery = true)    
    void insertOrder(@Param("entity") OrderEntity entity);
    
    
    @Transactional
    @Modifying
    @Query(value = "UPDATE milk_tea_shop_prod.Order SET " +
            "customer_id = :#{#entity.customerEntity?.id}, " +
            "employee_id = :#{#entity.employeeEntity.id}, " +
            "status = :#{#entity.status?.name()}, " +
            "total_amount = :#{#entity.totalAmount}, " +
            "final_amount = :#{#entity.finalAmount}, " +
            "order_time = :#{#entity.orderTime}, " +
            "customize_note = :#{#entity.customizeNote} " +
            "WHERE order_id = :#{#entity.id}",
            nativeQuery = true)
    void updateOrder(@Param("entity") OrderEntity entity);
    
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM milk_tea_shop_prod.Order WHERE order_id = :id",
            nativeQuery = true)
    void deleteOrder(@Param("id") Long id);

    @Query("select o from OrderEntity o where o.customerEntity.id is not null and o.customerEntity.id = :id")
    List<OrderEntity> findByCustomerEntity_IdNotNullAndCustomerEntity_Id(@Param("id") @NonNull Long id);

    @Query("select o from OrderEntity o where o.employeeEntity.id = :id")
    List<OrderEntity> findByEmployeeEntity_Id(@Param("id") @NonNull Long id);
    
    @EntityGraph(value = "OrderEntity.detail", type = EntityGraph.EntityGraphType.LOAD)
    Optional<OrderEntity> findDetailById(Long id);
}