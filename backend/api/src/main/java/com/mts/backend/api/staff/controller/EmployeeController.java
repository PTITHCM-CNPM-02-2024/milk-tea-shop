package com.mts.backend.api.staff.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.staff.request.CreateEmployeeRequest;
import com.mts.backend.api.staff.request.UpdateEmployeeRequest;
import com.mts.backend.application.staff.EmployeeCommandBus;
import com.mts.backend.application.staff.EmployeeQueryBus;
import com.mts.backend.application.staff.command.CreateEmployeeCommand;
import com.mts.backend.application.staff.command.DeleteEmpByIdCommand;
import com.mts.backend.application.staff.command.UpdateEmployeeCommand;
import com.mts.backend.application.staff.query.CheckoutTableByEmpIdQuery;
import com.mts.backend.application.staff.query.DefaultEmployeeQuery;
import com.mts.backend.application.staff.query.EmployeeByIdQuery;
import com.mts.backend.application.staff.query.GetEmpByAccountIdQuery;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.domain.account.value_object.Username;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.staff.identifier.EmployeeId;
import com.mts.backend.domain.staff.value_object.Position;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController implements IController {
    private final EmployeeCommandBus commandBus;
    private final EmployeeQueryBus queryBus;
    
    public EmployeeController(EmployeeCommandBus commandBus, EmployeeQueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createEmployee(@RequestBody CreateEmployeeRequest request) {
        var command = CreateEmployeeCommand.builder()
                .email(Email.builder().value(request.getEmail()).build())
                .firstName(FirstName.builder().value(request.getFirstName()).build())
                .lastName(LastName.builder().value(request.getLastName()).build())
                .phone(PhoneNumber.builder().value(request.getPhone()).build())
                .gender(Gender.valueOf(request.getGender()))
                .position(Position.builder().value(request.getPosition()).build())
                .username(Username.builder().value(request.getUsername()).build())
                .password(PasswordHash.builder().value(request.getPassword()).build())
                .roleId(RoleId.of(request.getRoleId()))
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateEmployee(@PathVariable("id") Long id, @RequestBody UpdateEmployeeRequest request) {
        var command = UpdateEmployeeCommand.builder()
                .id(EmployeeId.of(id))
                .email(Email.builder().value(request.getEmail()).build())
                .firstName(FirstName.builder().value(request.getFirstName()).build())
                .lastName(LastName.builder().value(request.getLastName()).build())
                .phone(PhoneNumber.builder().value(request.getPhone()).build())
                .gender(Gender.valueOf(request.getGender()))
                .position(Position.builder().value(request.getPosition()).build())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> getEmployee(@PathVariable("id") Long id) {
        var query = EmployeeByIdQuery.builder().id(EmployeeId.of(id)).build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> getEmployees(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        var request = DefaultEmployeeQuery.builder()
                .page(page)
                .size(size)
                .build();
        
        var result = queryBus.dispatch(request);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @GetMapping("/{id}/orders/order-tables")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> getOrderTablesByEmployeeId(@PathVariable("id") Long id,
                                                       @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        var query = CheckoutTableByEmpIdQuery.builder().employeeId(EmployeeId.of(id)).page(page).size(size).build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deleteEmployee(@PathVariable("id") Long id) {
        DeleteEmpByIdCommand command = DeleteEmpByIdCommand.builder()
                .id(EmployeeId.of(id))
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @GetMapping("/account/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> getEmployeeByAccountId(@PathVariable("id") Long id) {
        var query = GetEmpByAccountIdQuery.builder().accountId(AccountId.of(id)).build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
