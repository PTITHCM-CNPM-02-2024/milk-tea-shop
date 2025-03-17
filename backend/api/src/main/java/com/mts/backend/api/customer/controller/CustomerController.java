package com.mts.backend.api.customer.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.customer.request.CreateCustomerRequest;
import com.mts.backend.api.customer.request.UpdateCustomerRequest;
import com.mts.backend.application.customer.CustomerCommandBus;
import com.mts.backend.application.customer.CustomerQueryBus;
import com.mts.backend.application.customer.command.CreateCustomerCommand;
import com.mts.backend.application.customer.command.UpdateCustomerCommand;
import com.mts.backend.application.customer.command.UpdateMemberForCustomer;
import com.mts.backend.application.customer.query.CustomerByIdQuery;
import com.mts.backend.application.customer.query.CustomerByPhoneQuery;
import com.mts.backend.application.customer.query.DefaultCustomerQuery;
import com.mts.backend.application.customer.response.CustomerDetailResponse;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController implements IController {
    private final CustomerCommandBus customerCommandBus;
    private final CustomerQueryBus customerQueryBus;
    
    public CustomerController(CustomerCommandBus customerCommandBus, CustomerQueryBus customerQueryBus) {
        this.customerCommandBus = customerCommandBus;
        this.customerQueryBus = customerQueryBus;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createCustomer(@RequestBody CreateCustomerRequest request) {
        CreateCustomerCommand command = CreateCustomerCommand.builder().build();
        
        command.setEmail(request.getEmail());
        command.setFirstName(request.getFirstName());
        command.setLastName(request.getLastName());
        command.setGender(request.getGender());
        command.setPhone(request.getPhone());
        command.setAccountId(request.getAccountId());
        command.setMembershipId(request.getMemberId());
        
        var result = customerCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Long)result.getData())) : handleError(result);
        
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateCustomer(@PathVariable("id") Long id, @RequestBody UpdateCustomerRequest request){
        UpdateCustomerCommand command = UpdateCustomerCommand.builder()
                .id(request.getId())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .gender(request.getGender())
                .phone(request.getPhone())
                .build();
        
        var result = customerCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Long)result.getData())) : handleError(result);
    }
    
    @PutMapping("/{id}/membership")
    public ResponseEntity<ApiResponse<?>> updateMembership(@PathVariable("id") Long id, @RequestParam("membershipId") Integer membershipId){
        UpdateMemberForCustomer command = UpdateMemberForCustomer.builder()
                .customerId(id)
                .memberId(membershipId)
                .build();
        
        var result = customerCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Long)result.getData())) : handleError(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getCustomer(@PathVariable("id") Long id){
        var query = CustomerByIdQuery.builder().build();
        
        query.setId(id);
        
        var result = customerQueryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((CustomerDetailResponse)result.getData())) : handleError(result);
    }
    
    
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getCustomers(){
        var request = DefaultCustomerQuery.builder().build();
        
        var result = customerQueryBus.dispatch(request);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((List<CustomerDetailResponse>)result.getData())) : handleError(result);
    }
    
    @GetMapping("/search/phone")
    public ResponseEntity<ApiResponse<?>> getCustomerByPhone(@RequestParam("phone") String phone){
        var request = CustomerByPhoneQuery.builder().phone(phone).build();
        
        var result = customerQueryBus.dispatch(request);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((CustomerDetailResponse)result.getData())) : handleError(result);
    }
}
