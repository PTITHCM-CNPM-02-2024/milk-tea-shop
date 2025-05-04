package com.mts.backend.api.store.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.store.request.CreateStoreRequest;
import com.mts.backend.api.store.request.UpdateStoreRequest;
import com.mts.backend.application.store.StoreCommandBus;
import com.mts.backend.application.store.StoreQueryBus;
import com.mts.backend.application.store.command.CreateStoreCommand;
import com.mts.backend.application.store.command.UpdateStoreCommand;
import com.mts.backend.application.store.query.DefaultStoreQuery;
import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.PhoneNumber;
import com.mts.backend.domain.store.identifier.StoreId;
import com.mts.backend.domain.store.value_object.Address;
import com.mts.backend.domain.store.value_object.StoreName;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/v1/store")
@Tag(name = "Store Controller", description = "Store")
public class StoreController implements IController {
    
    private final StoreCommandBus commandBus;
    private final StoreQueryBus queryBus;
    
    public StoreController(StoreCommandBus commandBus, StoreQueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createStore(@RequestBody CreateStoreRequest request) {
        var command = CreateStoreCommand.builder()
                .name(StoreName.of(request.getName()))
                .address(Address.of(request.getAddress()))
                .phone(PhoneNumber.of(request.getPhone()))
                .email(Email.of(request.getEmail()))
                .openingDate(LocalDate.parse(request.getOpeningDate()))
                .taxCode(request.getTaxCode())
                .openTime(LocalTime.parse(request.getOpenTime(), DateTimeFormatter.ofPattern("HH:mm:ss")))
                .closeTime(LocalTime.parse(request.getCloseTime(), DateTimeFormatter.ofPattern("HH:mm:ss")))
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateStore(@PathVariable("id") Integer id,  @RequestBody UpdateStoreRequest request) {
        var command = UpdateStoreCommand.builder()
                .id(StoreId.of(id))
                .name(StoreName.of(request.getName()))
                .address(Address.of(request.getAddress()))
                .phone(PhoneNumber.of(request.getPhone()))
                .email(Email.of(request.getEmail()))
                .openingDate(LocalDate.parse(request.getOpeningDate()))
                .taxCode(request.getTaxCode())
                .openTime(LocalTime.parse(request.getOpenTime(), DateTimeFormatter.ofPattern("HH:mm:ss")))
                .closeTime(LocalTime.parse(request.getCloseTime(), DateTimeFormatter.ofPattern("HH:mm:ss")))    
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) :
                handleError(result);
    }
    
    @GetMapping("/info")
    public ResponseEntity<?> getStoreById() {
        var query = DefaultStoreQuery.builder().build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) :
                handleError(result);
    }
}
