package com.mts.backend.api.store.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.store.request.CreateServiceTableRequest;
import com.mts.backend.api.store.request.UpdateServiceTableRequest;
import com.mts.backend.application.store.ServiceTableCommandBus;
import com.mts.backend.application.store.ServiceTableQueryBus;
import com.mts.backend.application.store.command.CreateServiceTableCommand;
import com.mts.backend.application.store.command.UpdateServiceTableCommand;
import com.mts.backend.application.store.query.DefaultServiceTableQuery;
import com.mts.backend.application.store.query.ServiceTableActiveQuery;
import com.mts.backend.application.store.query.ServiceTableByIdQuery;
import com.mts.backend.application.store.response.ServiceTableDetailResponse;
import com.mts.backend.domain.store.ServiceTable;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/service-tables")
public class ServiceTableController implements IController {
    private final ServiceTableCommandBus commandBus;
    private final ServiceTableQueryBus queryBus;
    
    public ServiceTableController(ServiceTableCommandBus commandBus, ServiceTableQueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createServiceTable(@RequestBody CreateServiceTableRequest request) {
        var command = CreateServiceTableCommand.builder()
                .name(request.getName())
                .areaId(request.getAreaId())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Integer) result.getData())) : handleError(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateServiceTable(@PathVariable("id") Integer id,
                                                             @RequestBody UpdateServiceTableRequest request) {
        var command = UpdateServiceTableCommand.builder()
                .id(id)
                .name(request.getName())
                .isActive(request.getIsActive())
                .areaId(request.getAreaId())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Integer)result.getData())) :
                handleError(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getServiceTableById(@PathVariable("id") Integer id) {
        var query = ServiceTableByIdQuery.builder().id(id).build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((ServiceTableDetailResponse) result.getData())) :
                handleError(result);
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<?>> getAllServiceTableActive() {
        var query = ServiceTableActiveQuery.builder().build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success( result.getData())) :
                handleError(result);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllServiceTable() {
        var query = DefaultServiceTableQuery.builder().build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success( result.getData())) :
                handleError(result);
    }
    
    
}
