package com.mts.backend.api.product.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.product.request.CreateUnitRequest;
import com.mts.backend.api.product.request.UpdateUnitRequest;
import com.mts.backend.application.product.UnitCommandBus;
import com.mts.backend.application.product.UnitQueryBus;
import com.mts.backend.application.product.command.CreateUnitCommand;
import com.mts.backend.application.product.command.UpdateUnitCommand;
import com.mts.backend.application.product.query.DefaultUnitQuery;
import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.value_object.UnitName;
import com.mts.backend.domain.product.value_object.UnitSymbol;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/units")
public class UnitController implements IController {
    
    private final UnitCommandBus unitCommandBus;
    private final UnitQueryBus unitQueryBus;
    
    public UnitController(UnitCommandBus unitCommandBus, UnitQueryBus unitQueryBus) {
        this.unitCommandBus = unitCommandBus;
        this.unitQueryBus = unitQueryBus;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createUnit(@RequestBody CreateUnitRequest createUnitRequest){
        CreateUnitCommand command =
                CreateUnitCommand.builder().name(UnitName.builder().value(createUnitRequest.getName()).build())
                        .description(createUnitRequest.getDescription())
                        .symbol(UnitSymbol.builder().value(createUnitRequest.getSymbol()).build())
                        .build();
        
        var result = unitCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Integer) result.getData())) : handleError(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Integer>> updateUnit(@PathVariable("id") Integer id, @RequestBody UpdateUnitRequest updateUnitRequest){
        UpdateUnitCommand command =
                UpdateUnitCommand.builder().id(UnitOfMeasureId.of(id))
                        .name(UnitName.builder().value(updateUnitRequest.getName()).build())
                        .description(updateUnitRequest.getDescription())
                        .symbol(UnitSymbol.builder().value(updateUnitRequest.getSymbol()).build())
                        .build();
        
        var result = unitCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Integer) result.getData(), "Thông tin đơn vị đã được cập nhật")) : handleError(result);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllUnit(){
        DefaultUnitQuery query = DefaultUnitQuery.builder().build();
        
        var result = unitQueryBus.dispatch(query);
            
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);
    }
}
