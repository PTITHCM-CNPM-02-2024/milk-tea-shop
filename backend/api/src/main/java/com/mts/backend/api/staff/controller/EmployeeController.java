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
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/employees")
public class EmployeeController implements IController {
    private final EmployeeCommandBus commandBus;
    private final EmployeeQueryBus queryBus;
    
    public EmployeeController(EmployeeCommandBus commandBus, EmployeeQueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createEmployee(@RequestBody CreateEmployeeRequest request) {
        var command = CreateEmployeeCommand.builder().build();
        
        command.setEmail(request.getEmail());
        command.setFirstName(request.getFirstName());
        command.setLastName(request.getLastName());
        command.setGender(request.getGender());
        command.setPhone(request.getPhone());
        command.setPosition(request.getPosition());
        command.setAccountId(request.getAccountId());
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Long) result.getData())) : handleError(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Long>> updateEmployee(@PathVariable("id") Long id, @RequestBody UpdateEmployeeRequest request) {
        var command = UpdateEmployeeCommand.builder().build();
        
        command.setId(id);
        command.setEmail(request.getEmail());
        command.setFirstName(request.getFirstName());
        command.setLastName(request.getLastName());
        command.setGender(request.getGender());
        command.setPhone(request.getPhone());
        command.setPosition(request.getPosition());
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Long) result.getData())) : handleError(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getEmployee(@PathVariable("id") Long id) {
        var query = EmployeeByIdQuery.builder().build();
        
        query.setId(id);
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((EmployeeDetailResponse) result.getData())) : handleError(result);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getEmployees() {
        var request = DefaultEmployeeQuery.builder().build();
        
        var result = queryBus.dispatch(request);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((List< EmployeeDetailResponse >) result.getData())) : handleError(result);
    }
}
