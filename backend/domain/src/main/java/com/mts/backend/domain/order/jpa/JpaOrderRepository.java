package com.mts.backend.domain.order.jpa;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.order.Order;
import com.mts.backend.domain.order.value_object.OrderStatus;
import com.mts.backend.domain.product.ProductPrice;

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

public interface JpaOrderRepository extends JpaRepository<Order, Long> {
    @Query("select o from Order o where o.employee.id = :id order by o.finalAmount DESC")
    Page<Order> findByEmployeeEntity_IdOrderByFinalAmountDesc(@Param("id") @NonNull Long id, Pageable pageable);

    @Query("""
            select o from Order o
            where o.employee.id = :id and (o.orderTime between :orderTimeStart and :orderTimeEnd) order by o.finalAmount DESC""")
    Page<Order> findByEmployeeEntity_IdAndOrderTimeBetween(@Param("id") @NonNull Long id, @Param("orderTimeStart") Instant orderTimeStart, @Param("orderTimeEnd") Instant orderTimeEnd, Pageable pageable);

    @EntityGraph(attributePaths = {"customer.membershipType.memberDiscountValue", "employee", "orderDiscounts.discount.coupon", "orderProducts.price.size", "orderTables.table"})
    @Query("select o from Order o where o.id = :id")
    Optional<Order> findOrderWithDetails(@Param("id") @NonNull Long id);

    @Query("""
            select count(o) from Order o inner join o.orderDiscounts orderDiscounts
            where o.customer.id = :id and orderDiscounts.discount.id = :id1 and o.status = :status""")
    long countByCustomerEntity_IdAndOrderDiscounts_Discount_IdAndStatus(@Param("id") @NonNull Long id,
                                                                        @Param("id1") @NonNull Long id1,
                                                                        @Param("status") @NonNull OrderStatus status);

    @Query("""
            select count(o) from Order o inner join o.orderProducts orderProducts
            where orderProducts.price = :productPrice and o.status = :status""")
    long countByOrderProducts_ProductPriceEntityAndStatus(@Param("productPrice") @NonNull ProductPrice productPrice, @Param("status") @NonNull OrderStatus status);


    @Query("select count(DISTINCT o) from Order o inner join o.orderDiscounts od " +
            "where o.customer.id = :customerId and o.status = 'COMPLETED' " +
            "and od.discount.id = :discountId")
    long countCompletedOrdersWithSpecificDiscount(
            @Param("customerId") Long customerId,
            @Param("discountId") Long discountId
    );
    


    @Query("select o from Order o where o.customer.id is not null and o.customer.id = :id")
    List<Order> findByCustomerEntity_IdNotNullAndCustomerEntity_Id(@Param("id") @NonNull Long id);

    @EntityGraph(attributePaths = {"customer", "employee", "orderTables.table"})
    @Query("select o from Order o join o.orderTables ot where o.employee.id = :id " +
            "and o.status = 'COMPLETED' and size(o.orderTables) > 0 " +
            "and ot.checkOut is null")
    List<Order> findByEmployeeEntity_IdFetchOrdTbs(@Param("id") @NonNull Long id);

    @EntityGraph(attributePaths = {"customer", "employee", "orderTables.table"})
    @Query("select o from Order o join o.orderTables ot where o.employee.id = :id " +
            "and o.status = 'COMPLETED' and size(o.orderTables) > 0 " +
            "and ot.checkOut is null")
    Page<Order> findByEmployeeEntity_IdFetchOrdTbs(@Param("id") @NonNull Long id, @Param("status") @NonNull OrderStatus status, Pageable pageable);
    
    
    @EntityGraph(value = "graph.order.fetchEmpCus", type = EntityGraph.EntityGraphType.FETCH)
    @Query("select o from Order o")
    Page<Order> findAllFetchEmpCus(Pageable pageable);
    
    @EntityGraph(attributePaths = {"customer.membershipType.memberDiscountValue", "employee", "orderDiscounts.discount.coupon", "orderDiscounts.discount.promotionDiscountValue", "orderProducts.price.product.productPrices", "orderProducts.price.size", "orderTables.table", "payments.paymentMethod"})
    @Query("select o from Order o where o.id = :id")
    Optional<Order> findByIdFetch(@Param("id") Long id);
    
    @Query("""
            SELECT  SUM(o.finalAmount) 
            from Order o 
           """)
    BigDecimal getTotalOrderValue();
    
    @Query("""
            SELECT  SUM(o.finalAmount)
            from Order o
            where o.orderTime BETWEEN :startDate AND :endDate AND o.employee.id = :employeeId
           """)
    BigDecimal getSumRevenueByEmpId(@Param("employeeId") Long employeeId,@Param("startDate") Instant startDate,
                                            @Param("endDate") Instant endDate);

    @Query("""
            SELECT  AVG(o.finalAmount)
            from Order o
            where o.orderTime BETWEEN :startDate AND :endDate AND o.employee.id = :employeeId
           """)
    BigDecimal getAvgRevenueByEmpId(@Param("employeeId") Long employeeId,@Param("startDate") Instant startDate,
                                    @Param("endDate") Instant endDate);

    @Query("""
            SELECT  MAX(o.finalAmount)
            from Order o
            where o.orderTime BETWEEN :startDate AND :endDate AND o.employee.id = :employeeId
           """)
    BigDecimal getMaxRevenueByEmpId(@Param("employeeId") Long employeeId,@Param("startDate") Instant startDate,
                                    @Param("endDate") Instant endDate);

    @Query("""
            SELECT  MIN(o.finalAmount)
            from Order o
            where o.orderTime BETWEEN :startDate AND :endDate AND o.employee.id = :employeeId
           """)
    BigDecimal getMinRevenueByEmpId(@Param("employeeId") Long employeeId,@Param("startDate") Instant startDate,
                               @Param("endDate") Instant endDate);
    
    @Query("""
            SELECT  AVG(o.finalAmount) 
            from Order o 
           """)
    BigDecimal getAvgOrderValue();

    @Query("""
            SELECT  MIN(o.finalAmount) 
            from Order o 
           """)
    BigDecimal getMinOrderValue();
    
    @Query("""
            SELECT  MAX(o.finalAmount)
            from Order o 
           """)
    BigDecimal getMaxOrderValue();
    @Query("""
    SELECT c.name AS categoryName, o.finalAmount AS finalAmount, o.id AS orderId
    FROM Order o
    JOIN o.orderProducts op
    JOIN op.price pp
    JOIN pp.product p
    JOIN p.category c
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
    FROM Order o
    JOIN o.orderProducts op
    JOIN op.price pp
    JOIN pp.product p
    RIGHT JOIN p.category c
    WHERE o.orderTime BETWEEN :startDate AND :endDate
    GROUP BY c.name
    ORDER BY totalRevenue DESC
    """)
    List<Object[]> findFinalAmountsByCategoryAndDateRange(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);


    @Query("""
    SELECT CAST(o.orderTime AS date) as get_date, SUM(o.finalAmount) as totalRevenue
    FROM Order o
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
    SELECT CAST(o.orderTime AS date) as get_date, SUM(o.finalAmount) as totalRevenue
    FROM Order o
    WHERE o.orderTime BETWEEN :fromDate AND :toDate AND o.employee.id = :employeeId
    GROUP BY CAST(o.orderTime AS date)
    ORDER BY get_date
    """)
    List<Object[]> findRevenueByTimeRange(
            @Param("employeeId") Long employeeId,
            @Param("fromDate") Instant fromDate,
            @Param("toDate") Instant toDate
    );

    @Query(value = """

         SELECT
        p.product_id as productId,
        p.name as productName,
        COALESCE(c.name, 'Không có danh mục') as categoryName,
        SUM(op.quantity) as totalQuantity,
        SUM(op.quantity * pp.price) as totalRevenue
    FROM `order` o
    JOIN order_product op ON o.order_id = op.order_id
    JOIN product_price pp ON op.product_price_id = pp.product_price_id
    JOIN product p ON pp.product_id = p.product_id
    LEFT JOIN category c ON p.category_id = c.category_id
    WHERE o.status = 'COMPLETED' AND
     o.order_time BETWEEN :fromDate AND :toDate
    GROUP BY p.product_id, p.name, c.name
    ORDER BY totalRevenue DESC""", nativeQuery = true)
    List<Object[]> findTopSaleByProduct(
            @Param("fromDate") Instant fromDate,
            @Param("toDate") Instant toDate,
            Pageable pageable
    );
    

}