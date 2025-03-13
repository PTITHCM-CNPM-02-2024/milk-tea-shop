package com.mts.backend.api.product.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.product.request.CreateUnitRequest;
import com.mts.backend.api.product.request.UpdateUnitRequest;
import com.mts.backend.application.product.UnitCommandBus;
import com.mts.backend.application.product.command.CreateUnitCommand;
import com.mts.backend.application.product.command.UpdateUnitCommand;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/units")
public class UnitController implements IController {
    
    private final UnitCommandBus unitCommandBus;
    
    public UnitController(UnitCommandBus unitCommandBus) {
        this.unitCommandBus = unitCommandBus;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Integer>> createUnit(@RequestBody CreateUnitRequest createUnitRequest){
        CreateUnitCommand command = CreateUnitCommand.builder().name(createUnitRequest.getName()).description(createUnitRequest.getDescription()).symbol(createUnitRequest.getSymbol()).build();
        
        var result = unitCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Integer) result.getData())) : handleError(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Integer>> updateUnit(@PathVariable("id") Integer id, @RequestBody UpdateUnitRequest updateUnitRequest){
        UpdateUnitCommand command = UpdateUnitCommand.builder().id(id).name(updateUnitRequest.getName()).description(updateUnitRequest.getDescription()).symbol(updateUnitRequest.getSymbol()).build();
        
        var result = unitCommandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Integer) result.getData(), "Thông tin đơn vị đã được cập nhật")) : handleError(result);
    }
}
