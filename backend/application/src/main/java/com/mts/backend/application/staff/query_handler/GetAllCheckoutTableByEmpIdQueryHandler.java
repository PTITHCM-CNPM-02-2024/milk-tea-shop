package com.mts.backend.application.staff.query_handler;

import com.mts.backend.application.staff.query.CheckoutTableByEmpIdQuery;
import com.mts.backend.application.order.response.OrderDetailResponse;
import com.mts.backend.application.order.response.OrderTableDetailResponse;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.customer.CustomerEntity;
import com.mts.backend.domain.order.jpa.JpaOrderRepository;
import com.mts.backend.domain.staff.jpa.JpaEmployeeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class GetAllCheckoutTableByEmpIdQueryHandler implements IQueryHandler<CheckoutTableByEmpIdQuery, CommandResult> {
    private final JpaOrderRepository orderRepository;
    private final JpaEmployeeRepository employeeRepository;
    public GetAllCheckoutTableByEmpIdQueryHandler(JpaOrderRepository orderRepository,
                                                  JpaEmployeeRepository employeeRepository, JpaEmployeeRepository employeeRepository1) {
        this.orderRepository = orderRepository;
        this.employeeRepository = employeeRepository1;
    }
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(CheckoutTableByEmpIdQuery query) {
        Objects.requireNonNull(query, "CheckoutTableByEmpIdQuery is required");
        
        var orders = orderRepository.findByEmployeeEntity_IdFetchOrdTbs(query.getEmployeeId().getValue());
        
        List<OrderDetailResponse> orderDetailResponses = orders.stream()
                .map(order -> OrderDetailResponse.builder()
                        .orderId(order.getId())
                        .employeeId(order.getEmployeeEntity().getId())
                        .employeeName(order.getEmployeeEntity().getFullName())
                        .customerName(order.getCustomerEntity().flatMap(CustomerEntity::getFullName)
                                .orElse(null))
                        .orderStatus(order.getStatus().map(Enum::name).orElse(null))
                        .totalAmount(order.getTotalAmount().map(Money::getValue).orElse(null))
                        .finalAmount(order.getFinalAmount().map(Money::getValue).orElse(null))
                        .orderTime(order.getOrderTime())
                        .orderTables(order.getOrderTables().stream()
                                .map(orderTable -> OrderTableDetailResponse.builder()
                                        .tableNumber(orderTable.getTable().getTableNumber().getValue())
                                        .checkIn(orderTable.getCheckIn())
                                        .checkOut(orderTable.getCheckOut())
                                        .build())
                                .toList())
                        .build())
                .toList();
        
        return CommandResult.success(orderDetailResponses);
    }
}
