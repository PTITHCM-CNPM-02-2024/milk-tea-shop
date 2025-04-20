package com.mts.backend.application.report.query_handler;

import com.mts.backend.application.report.query.BasicReportQuery;
import com.mts.backend.application.report.response.OverviewReportResponse;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.customer.jpa.JpaCustomerRepository;
import com.mts.backend.domain.order.jpa.JpaOrderRepository;
import com.mts.backend.domain.product.jpa.JpaProductRepository;
import com.mts.backend.domain.staff.jpa.JpaEmployeeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

@Service
public class GetOverviewReportQueryHandler implements IQueryHandler<BasicReportQuery, CommandResult> {
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(BasicReportQuery query){
        var response = OverviewReportResponse.builder()
                .totalProduct(productRepository.count())
                .totalEmployee(employeeRepository.count())
                .totalOrder(orderRepository.count())
                .totalCustomer(customerRepository.count())
                .build();
        
        var totalOrderValue = orderRepository.getTotalOrderValue();
        
        var avgOrderValue = orderRepository.getAvgOrderValue();
        
        var minOrderValue = orderRepository.getMinOrderValue();
        
        var maxOrderValue = orderRepository.getMaxOrderValue();
        
        
        response.setSumOrderValue(totalOrderValue);
        response.setAvgOrderValue(avgOrderValue);
        response.setMinOrderValue(minOrderValue);
        response.setMaxOrderValue(maxOrderValue);
        
        return CommandResult.success(response);
        
        
    }

    private final JpaProductRepository productRepository;
    private final JpaEmployeeRepository employeeRepository;
    private final JpaOrderRepository orderRepository;
    private final JpaCustomerRepository customerRepository;
    public GetOverviewReportQueryHandler(JpaProductRepository productRepository,
                                         JpaEmployeeRepository employeeRepository,
                                         JpaOrderRepository orderRepository, JpaCustomerRepository customerRepository) {
        this.productRepository = productRepository;
        this.employeeRepository = employeeRepository;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }
    
    
}
