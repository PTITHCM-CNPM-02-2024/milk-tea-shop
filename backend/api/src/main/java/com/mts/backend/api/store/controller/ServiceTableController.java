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
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.domain.store.value_object.TableNumber;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

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
                .name(TableNumber.builder().value(request.getName()).build())
                .areaId(AreaId.of(request.getAreaId()))
                .description(request.getDescription())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateServiceTable(@PathVariable("id") Integer id,
                                                             @RequestBody UpdateServiceTableRequest request) {
        var command = UpdateServiceTableCommand.builder()
                .id(ServiceTableId.of(request.getId()))
                .name(TableNumber.builder().value(request.getName()).build())
                .isActive(request.getIsActive())
                .areaId(Objects.isNull(request.getAreaId()) ? null : AreaId.of(request.getAreaId()))
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Integer)result.getData())) :
                handleError(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getServiceTableById(@PathVariable("id") Integer id) {
        var query = ServiceTableByIdQuery.builder().id(ServiceTableId.of(id)).build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((ServiceTableDetailResponse) result.getData())) :
                handleError(result);
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<?>> getAllServiceTableActive(@RequestParam(value = "active", defaultValue = 
            "true") Boolean active, @RequestParam(value = "size", defaultValue = "10") Integer size) {
        var query = ServiceTableActiveQuery.builder().active(active).size(size).build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success( result.getData())) :
                handleError(result);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllServiceTable(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                             @RequestParam(value = "size", defaultValue = "40") Integer size) {
        var query = DefaultServiceTableQuery.builder().page(page).size(size).build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success( result.getData())) :
                handleError(result);
    }
    
    
}
