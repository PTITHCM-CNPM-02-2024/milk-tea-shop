package com.mts.backend.api.customer.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.customer.request.CreateCustomerRequest;
import com.mts.backend.api.customer.request.UpdateCustomerRequest;
import com.mts.backend.application.customer.CustomerCommandBus;
import com.mts.backend.application.customer.CustomerQueryBus;
import com.mts.backend.application.customer.command.CreateCustomerCommand;
import com.mts.backend.application.customer.command.DeleteCusByIdCommand;
import com.mts.backend.application.customer.command.UpdateCustomerCommand;
import com.mts.backend.application.customer.command.UpdateMemberForCustomer;
import com.mts.backend.application.customer.query.CustomerByIdQuery;
import com.mts.backend.application.customer.query.CustomerByPhoneQuery;
import com.mts.backend.application.customer.query.DefaultCustomerQuery;
import com.mts.backend.application.customer.response.CustomerDetailResponse;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.domain.account.value_object.Username;
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
    public ResponseEntity<?> createCustomer(@RequestBody CreateCustomerRequest request) {
            CreateCustomerCommand command = CreateCustomerCommand.builder()
                .firstName(Objects.isNull(request.getFirstName()) ? null : FirstName.builder().value(request.getFirstName()).build())
                .lastName(Objects.isNull(request.getLastName()) ? null : LastName.builder().value(request.getLastName()).build())
                .email(Objects.isNull(request.getEmail()) ? null : Email.builder().value(request.getEmail()).build())
                .gender(Objects.isNull(request.getGender()) ? null : Gender.valueOf(request.getGender()))
                .phone(PhoneNumber.builder().value(request.getPhone()).build())
                    .membershipId(Objects.isNull(request.getMemberId()) ? null:
                            MembershipTypeId.of(request.getMemberId()))
                .build();
            
         if (!Objects.isNull(request.getUsername()) && !Objects.isNull(request.getPassword()))  { 
            command.setUsername(Username.builder().value(request.getUsername()).build());
            command.setPasswordHash(PasswordHash.builder().value(request.getPassword()).build());
            command.setRoleId(RoleId.of(request.getRoleId()));}
                
        
        var result = customerCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
        
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable("id") Long id, @RequestBody UpdateCustomerRequest request){
        UpdateCustomerCommand command = UpdateCustomerCommand.builder()
                .id(CustomerId.of(id))
                .email(Objects.isNull(request.getEmail()) ? null : Email.builder().value(request.getEmail()).build())
                .firstName(Objects.isNull(request.getFirstName()) ? null :
                        FirstName.builder().value(request.getFirstName()).build())
                .lastName(Objects.isNull(request.getLastName()) ? null :
                        LastName.builder().value(request.getLastName()).build())
                .phone(PhoneNumber.builder().value(request.getPhone()).build())
                .gender(Objects.isNull(request.getGender()) ?  null : Gender.valueOf(request.getGender())) 
                .build();
        
        var result = customerCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @PutMapping("/{id}/membership")
    public ResponseEntity<?> updateMembership(@PathVariable("id") Long id, @RequestParam("value") Integer value){
        UpdateMemberForCustomer command = UpdateMemberForCustomer.builder()
                .customerId(CustomerId.of(id))
                .memberId(MembershipTypeId.of(value))
                .build();
        
        var result = customerCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomer(@PathVariable("id") Long id){
        var query = CustomerByIdQuery.builder().
                id(CustomerId.of(id))
                .build();
        
        var result = customerQueryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    
    @GetMapping
    public ResponseEntity<?> getCustomers(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", defaultValue = "10") Integer size){
        var request = DefaultCustomerQuery.builder().
                page(page)
                .size(size)
                .build();
        
        var result = customerQueryBus.dispatch(request);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @GetMapping("/search/phone")
    public ResponseEntity<?> getCustomerByPhone(@RequestParam("phone") String phone){
        var request = CustomerByPhoneQuery.builder().phone(PhoneNumber.builder().value(phone).build()).build();
        
        var result = customerQueryBus.dispatch(request);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable("id") Long id) {
        DeleteCusByIdCommand command = DeleteCusByIdCommand.builder()
                .customerId(CustomerId.of(id))
                .build();

        var result = customerCommandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
