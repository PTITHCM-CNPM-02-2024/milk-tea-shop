package com.mts.backend.api.store.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.store.request.CreateStoreRequest;
import com.mts.backend.api.store.request.UpdateStoreRequest;
import com.mts.backend.application.store.StoreCommandBus;
import com.mts.backend.application.store.StoreQueryBus;
import com.mts.backend.application.store.command.CreateStoreCommand;
import com.mts.backend.application.store.command.UpdateStoreCommand;
import com.mts.backend.application.store.query.StoreByIdQuery;
import com.mts.backend.application.store.response.StoreDetailResponse;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/store")
public class StoreController implements IController {
    
    private final StoreCommandBus commandBus;
    private final StoreQueryBus queryBus;
    
    public StoreController(StoreCommandBus commandBus, StoreQueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createStore(@RequestBody CreateStoreRequest request) {
        var command = CreateStoreCommand.builder()
                .name(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .openingDate(LocalDateTime.parse(request.getOpeningDate()))
                .taxCode(request.getTaxCode())
                .openTime(LocalDateTime.parse(request.getOpenTime()).toLocalTime())
                .closeTime(LocalDateTime.parse(request.getCloseTime()).toLocalTime())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Integer) result.getData())) : handleError(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateStore(@PathVariable("id") Integer id,
                                                      @RequestBody UpdateStoreRequest request) {
        var command = UpdateStoreCommand.builder()
                .id(id)
                .name(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .openingDate(LocalDateTime.parse(request.getOpeningDate()))
                .taxCode(request.getTaxCode())
                .openTime(LocalDateTime.parse(request.getOpenTime()).toLocalTime())
                .closeTime(LocalDateTime.parse(request.getCloseTime()).toLocalTime())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Integer)result.getData())) :
                handleError(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getStoreById(@PathVariable("id") Integer id) {
        var query = StoreByIdQuery.builder().id(id).build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((StoreDetailResponse) result.getData())) :
                handleError(result);
    }
}
