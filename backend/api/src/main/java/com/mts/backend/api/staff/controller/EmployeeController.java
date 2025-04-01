package com.mts.backend.api.staff.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.staff.request.CreateEmployeeRequest;
import com.mts.backend.api.staff.request.UpdateEmployeeRequest;
import com.mts.backend.application.staff.EmployeeCommandBus;
import com.mts.backend.application.staff.EmployeeQueryBus;
import com.mts.backend.application.staff.command.CreateEmployeeCommand;
import com.mts.backend.application.staff.command.UpdateEmployeeCommand;
import com.mts.backend.application.staff.query.DefaultEmployeeQuery;
import com.mts.backend.application.staff.query.EmployeeByIdQuery;
import com.mts.backend.application.staff.response.EmployeeDetailResponse;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.staff.identifier.EmployeeId;
import com.mts.backend.domain.staff.value_object.Position;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<Long>> createEmployee(@RequestBody CreateEmployeeRequest request) {
        var command = CreateEmployeeCommand.builder()
                .accountId(AccountId.of(request.getAccountId()))
                .email(Email.builder().value(request.getEmail()).build())
                .firstName(FirstName.builder().value(request.getFirstName()).build())
                .lastName(LastName.builder().value(request.getLastName()).build())
                .phone(PhoneNumber.builder().value(request.getPhone()).build())
                .gender(Gender.valueOf(request.getGender()))
                .position(Position.builder().value(request.getPosition()).build())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Long) result.getData())) : handleError(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Long>> updateEmployee(@PathVariable("id") Long id, @RequestBody UpdateEmployeeRequest request) {
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
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Long) result.getData())) : handleError(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getEmployee(@PathVariable("id") Long id) {
        var query = EmployeeByIdQuery.builder().id(EmployeeId.of(id)).build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((EmployeeDetailResponse) result.getData())) : handleError(result);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getEmployees(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        var request = DefaultEmployeeQuery.builder()
                .page(page)
                .size(size)
                .build();
        
        var result = queryBus.dispatch(request);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);
    }
}
