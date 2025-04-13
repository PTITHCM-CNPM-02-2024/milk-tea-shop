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
    public ResponseEntity<?> createUnit(@RequestBody CreateUnitRequest createUnitRequest){
        CreateUnitCommand command =
                CreateUnitCommand.builder().name(UnitName.builder().value(createUnitRequest.getName()).build())
                        .description(createUnitRequest.getDescription())
                        .symbol(UnitSymbol.builder().value(createUnitRequest.getSymbol()).build())
                        .build();
        
        var result = unitCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUnit(@PathVariable("id") Integer id, @RequestBody UpdateUnitRequest updateUnitRequest){
        UpdateUnitCommand command =
                UpdateUnitCommand.builder().id(UnitOfMeasureId.of(id))
                        .name(UnitName.builder().value(updateUnitRequest.getName()).build())
                        .description(updateUnitRequest.getDescription())
                        .symbol(UnitSymbol.builder().value(updateUnitRequest.getSymbol()).build())
                        .build();
        
        var result = unitCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllUnit(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        DefaultUnitQuery query = DefaultUnitQuery.builder()
                .page(page)
                .size(size)
                .build();
        
        var result = unitQueryBus.dispatch(query);
            
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
