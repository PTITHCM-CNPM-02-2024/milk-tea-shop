package com.mts.backend.api.order.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderBaseRequest {
    protected Long employeeId;
    protected Long customerId;
    protected String note;
    protected List<OrderProductBaseRequest> products;
    protected List<OrderTableBaseRequest> tables;
    protected List<OrderDiscountBaseRequest> discounts;
    
    public List<OrderProductBaseRequest> getProducts(){
        if (products == null){
            products = new ArrayList<>();
        }
        return products;
    }
    
    public List<OrderTableBaseRequest> getTables(){
        if (tables == null){
            tables = new ArrayList<>();
        }
        return tables;
    }
    
    public List<OrderDiscountBaseRequest> getDiscounts(){
        if (discounts == null){
            discounts = new ArrayList<>();
        }
        return discounts;
    }
}
