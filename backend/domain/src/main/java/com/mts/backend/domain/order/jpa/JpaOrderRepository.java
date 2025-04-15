package com.mts.backend.domain.order.jpa;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.order.OrderEntity;
import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.domain.product.ProductPriceEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.Instant;
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

    @Query("""
            select count(o) from OrderEntity o inner join o.orderProducts orderProducts
            where orderProducts.productPriceEntity = :productPriceEntity and o.status = :status""")
    long countByOrderProducts_ProductPriceEntityAndStatus(@Param("productPriceEntity") @NonNull ProductPriceEntity productPriceEntity, @Param("status") @NonNull OrderStatus status);


    @Query("select count(DISTINCT o) from OrderEntity o inner join o.orderDiscounts od " +
            "where o.customerEntity.id = :customerId and o.status = 'COMPLETED' " +
            "and od.discount.id = :discountId")
    long countCompletedOrdersWithSpecificDiscount(
            @Param("customerId") Long customerId,
            @Param("discountId") Long discountId
    );
    


    @Query("select o from OrderEntity o where o.customerEntity.id is not null and o.customerEntity.id = :id")
    List<OrderEntity> findByCustomerEntity_IdNotNullAndCustomerEntity_Id(@Param("id") @NonNull Long id);

    @EntityGraph(attributePaths = {"customerEntity", "employeeEntity", "orderTables.table"})
    @Query("select o from OrderEntity o join o.orderTables ot where o.employeeEntity.id = :id " +
            "and o.status = 'COMPLETED' and size(o.orderTables) > 0 " +
            "and ot.checkOut is null")
    List<OrderEntity> findByEmployeeEntity_IdFetchOrdTbs(@Param("id") @NonNull Long id);

    @EntityGraph(attributePaths = {"customerEntity", "employeeEntity", "orderTables.table"})
    @Query("select o from OrderEntity o join o.orderTables ot where o.employeeEntity.id = :id " +
            "and o.status = 'COMPLETED' and size(o.orderTables) > 0 " +
            "and ot.checkOut is null")
    Page<OrderEntity> findByEmployeeEntity_IdFetchOrdTbs(@Param("id") @NonNull Long id, @Param("status") @NonNull OrderStatus status, Pageable pageable);
    
    
    @EntityGraph(value = "graph.order.fetchEmpCus", type = EntityGraph.EntityGraphType.FETCH)
    @Query("select o from OrderEntity o")
    Page<OrderEntity> findAllFetchEmpCus(Pageable pageable);
    
    @EntityGraph(attributePaths = {"customerEntity", "employeeEntity", "orderDiscounts.discount.couponEntity", "orderDiscounts.discount.promotionDiscountValue", "orderProducts.productPriceEntity.productEntity", "orderTables.table", "payments.paymentMethod"})
    @Query("select o from OrderEntity o where o.id = :id")
    Optional<OrderEntity> findByIdFetch(@Param("id") Long id);
    
    @Query("""
            SELECT  SUM(o.finalAmount) 
            from OrderEntity o 
           """)
    BigDecimal getTotalOrderValue();
    
    @Query("""
            SELECT  AVG(o.finalAmount) 
            from OrderEntity o 
           """)
    BigDecimal getAvgOrderValue();

    @Query("""
            SELECT  MIN(o.finalAmount) 
            from OrderEntity o 
           """)
    Money getMinOrderValue();
    
    @Query("""
            SELECT  MAX(o.finalAmount) 
            from OrderEntity o 
           """)
    Money getMaxOrderValue();
    @Query("""
    SELECT c.name AS categoryName, o.finalAmount AS finalAmount, o.id AS orderId
    FROM OrderEntity o
    JOIN o.orderProducts op
    JOIN op.productPriceEntity pp
    JOIN pp.productEntity p
    JOIN p.categoryEntity c
    WHERE o.orderTime BETWEEN :startDate AND :endDate
    AND c.id = :categoryId
    ORDER BY o.id
    """)
    List<Object[]> findFinalAmountsByCategoryAndDateRange(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("categoryId") Integer categoryId
    );

    @Query("""
    SELECT c.name AS categoryName, SUM(op.quantity * pp.price) AS totalRevenue
    FROM OrderEntity o
    JOIN o.orderProducts op
    JOIN op.productPriceEntity pp
    JOIN pp.productEntity p
    RIGHT JOIN p.categoryEntity c
    WHERE o.orderTime BETWEEN :startDate AND :endDate
    GROUP BY c.name
    ORDER BY totalRevenue DESC
    """)
    List<Object[]> findFinalAmountsByCategoryAndDateRange(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);


    @Query("""
    SELECT CAST(o.orderTime AS date) as get_date, SUM(o.finalAmount) as totalRevenue
    FROM OrderEntity o
    WHERE o.orderTime BETWEEN :fromDate AND :toDate
    AND o.status = 'COMPLETED'
    GROUP BY CAST(o.orderTime AS date)
    ORDER BY get_date
    """)
    List<Object[]> findRevenueByTimeRange(
            @Param("fromDate") Instant fromDate,
            @Param("toDate") Instant toDate
    );

    @Query("""
    SELECT
        p.id AS maSanPham, 
        p.name AS tenSanPham, 
        COALESCE(c.name, 'Không có danh mục') AS danhMuc,
        SUM(op.quantity) AS soLuongBan,
        SUM(op.quantity * pp.price) AS doanhThu
    FROM OrderEntity o
    JOIN o.orderProducts op
    JOIN op.productPriceEntity pp
    JOIN pp.productEntity p
    LEFT JOIN p.categoryEntity c
    WHERE o.status = 'COMPLETED'
    AND o.orderTime BETWEEN :fromDate AND :toDate
    GROUP BY p.id, p.name, c.name
    ORDER BY SUM(op.quantity) DESC
    """)
    List<Object[]> findTopSaleByProduct(
            @Param("fromDate") Instant fromDate,
            @Param("toDate") Instant toDate,
            Pageable pageable
    );
    

}