package com.mts.backend.application.order.command;

import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.staff.identifier.EmployeeId;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public class CreateOrderCommand implements ICommand<CommandResult> {
    
    private EmployeeId employeeId;
    private CustomerId customerId;
    private List<OrderProductCommand> orderProducts;
    private List<OrderTableCommand> orderTables;
    private List<OrderDiscountCommand> orderDiscounts;
    private String note;
    
    
    public Optional<CustomerId> getCustomerId(){
        return Optional.ofNullable(customerId);
    }
    
    public List<OrderProductCommand> getOrderProducts(){
        if (orderProducts == null){
            orderProducts = new ArrayList<>();
        }
        return orderProducts;
    }
    
    public List<OrderTableCommand> getOrderTables(){
        if (orderTables == null){
            orderTables = new ArrayList<>();
        }
        return orderTables;
    }
    
    public List<OrderDiscountCommand> getOrderDiscounts(){
        if (orderDiscounts == null){
            orderDiscounts = new ArrayList<>();
        }
        return orderDiscounts;
    }
    
    
}
