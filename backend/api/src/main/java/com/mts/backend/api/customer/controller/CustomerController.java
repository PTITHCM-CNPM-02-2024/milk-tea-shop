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
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

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
        CreateCustomerCommand command = CreateCustomerCommand.builder()
                .firstName(Objects.isNull(request.getFirstName()) ? null : FirstName.builder().value(request.getFirstName()).build())
                .lastName(Objects.isNull(request.getLastName()) ? null : LastName.builder().value(request.getLastName()).build())
                .accountId(Objects.isNull(request.getAccountId()) ? null : AccountId.of(request.getAccountId()))
                .email(Objects.isNull(request.getEmail()) ? null : Email.builder().value(request.getEmail()).build())
                .membershipId(Objects.isNull(request.getMemberId()) ? null : MembershipTypeId.of(request.getMemberId()))
                .gender(Objects.isNull(request.getGender()) ? null : Gender.valueOf(request.getGender()))
                .phone(PhoneNumber.builder().value(request.getPhone()).build())
                .build();
                
        
        var result = customerCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Long)result.getData())) : handleError(result);
        
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateCustomer(@PathVariable("id") Long id, @RequestBody UpdateCustomerRequest request){
        UpdateCustomerCommand command = UpdateCustomerCommand.builder()
                .id(CustomerId.of(id))
                .email(Objects.isNull(request.getEmail()) ? null : Email.builder().value(request.getEmail()).build())
                .firstName(Objects.isNull(request.getFirstName()) ? null :
                        FirstName.builder().value(request.getFirstName()).build())
                .lastName(Objects.isNull(request.getLastName()) ? null :
                        LastName.builder().value(request.getLastName()).build())
                .phone(PhoneNumber.builder().value(request.getPhone()).build())
                .build();
        
        var result = customerCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Long)result.getData())) : handleError(result);
    }
    
    @PutMapping("/{id}/membership")
    public ResponseEntity<ApiResponse<?>> updateMembership(@PathVariable("id") Long id, @RequestParam("membershipId") Integer membershipId){
        UpdateMemberForCustomer command = UpdateMemberForCustomer.builder()
                .customerId(CustomerId.of(id))
                .memberId(MembershipTypeId.of(membershipId))
                .build();
        
        var result = customerCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Long)result.getData())) : handleError(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getCustomer(@PathVariable("id") Long id){
        var query = CustomerByIdQuery.builder().
                id(CustomerId.of(id))
                .build();
        
        var result = customerQueryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((CustomerDetailResponse)result.getData())) : handleError(result);
    }
    
    
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getCustomers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "size", defaultValue = "10") int size){
        var request = DefaultCustomerQuery.builder().
                page(page)
                .size(size)
                .build();
        
        var result = customerQueryBus.dispatch(request);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);
    }
    
    @GetMapping("/search/phone")
    public ResponseEntity<ApiResponse<?>> getCustomerByPhone(@RequestParam("phone") String phone){
        var request = CustomerByPhoneQuery.builder().phone(PhoneNumber.builder().value(phone).build()).build();
        
        var result = customerQueryBus.dispatch(request);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((CustomerDetailResponse)result.getData())) : handleError(result);
    }
}
