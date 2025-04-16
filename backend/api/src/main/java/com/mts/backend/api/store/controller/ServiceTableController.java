package com.mts.backend.api.store.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.store.request.CreateServiceTableRequest;
import com.mts.backend.api.store.request.UpdateServiceTableRequest;
import com.mts.backend.application.store.ServiceTableCommandBus;
import com.mts.backend.application.store.ServiceTableQueryBus;
import com.mts.backend.application.store.command.CreateServiceTableCommand;
import com.mts.backend.application.store.command.DeleteServiceTableByIdCommand;
import com.mts.backend.application.store.command.UpdateServiceTableCommand;
import com.mts.backend.application.store.query.DefaultServiceTableQuery;
import com.mts.backend.application.store.query.ServiceTableActiveQuery;
import com.mts.backend.application.store.query.ServiceTableByIdQuery;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.domain.store.value_object.TableNumber;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/v1/service-tables")
@Tag(name = "Service Table Controller", description = "Service Table")
public class ServiceTableController implements IController {
    private final ServiceTableCommandBus commandBus;
    private final ServiceTableQueryBus queryBus;
    
    public ServiceTableController(ServiceTableCommandBus commandBus, ServiceTableQueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createServiceTable(@RequestBody CreateServiceTableRequest request) {
        var command = CreateServiceTableCommand.builder()
                .name(TableNumber.builder().value(request.getName()).build())
                .areaId(AreaId.of(request.getAreaId()))
                .description(request.getDescription())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateServiceTable(@PathVariable("id") Integer id,
                                                             @RequestBody UpdateServiceTableRequest request) {
        var command = UpdateServiceTableCommand.builder()
                .id(ServiceTableId.of(request.getId()))
                .name(TableNumber.builder().value(request.getName()).build())
                .isActive(request.getIsActive())
                .areaId(Objects.isNull(request.getAreaId()) ? null : AreaId.of(request.getAreaId()))
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) :
                handleError(result);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> getServiceTableById(@PathVariable("id") Integer id) {
        var query = ServiceTableByIdQuery.builder().id(ServiceTableId.of(id)).build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) :
                handleError(result);
    }
    
    @GetMapping("/active/{active}")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> getAllServiceTableActive(@PathVariable("active") Boolean active) {
        var query = ServiceTableActiveQuery.builder()
                .active(active)
                .build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) :
                handleError(result);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> getAllServiceTable(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                             @RequestParam(value = "size", defaultValue = "40") Integer size) {
        var query = DefaultServiceTableQuery.builder().page(page).size(size).build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) :
                handleError(result);
    }
    

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deleteServiceTable(@PathVariable("id") Integer id) {
        DeleteServiceTableByIdCommand command = DeleteServiceTableByIdCommand.builder()
                .serviceTableId(ServiceTableId.of(id))
                .build();

        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
