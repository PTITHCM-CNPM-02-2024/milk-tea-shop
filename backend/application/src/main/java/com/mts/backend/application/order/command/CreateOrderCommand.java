package com.mts.backend.application.order.command;

import com.mts.backend.domain.order.entity.OrderDiscount;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CreateOrderCommand implements ICommand<CommandResult> {
    
    private Long employeeId;
    private Long customerId;
    private List<OrderProductCommand> orderProducts;
    private List<OrderTableCommand> orderTables;
    private List<OrderDiscountCommand> orderDiscounts;
    private String note;
    
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
