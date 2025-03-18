package com.mts.backend.api.store.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.store.request.CreateAreaRequest;
import com.mts.backend.api.store.request.UpdateAreaRequest;
import com.mts.backend.api.store.request.UpdateMaxAndActiveRequest;
import com.mts.backend.application.store.AreaCommandBus;
import com.mts.backend.application.store.AreaQueryBus;
import com.mts.backend.application.store.command.CreateAreaCommand;
import com.mts.backend.application.store.command.UpdateAreaCommand;
import com.mts.backend.application.store.command.UpdateAreaMaxAndActiveCommand;
import com.mts.backend.application.store.query.AreaActiveQuery;
import com.mts.backend.application.store.query.AreaByIdQuery;
import com.mts.backend.application.store.query.DefaultAreaQuery;
import com.mts.backend.application.store.query_handler.GetAllAreaActiveQueryHandler;
import com.mts.backend.application.store.response.AreaDetailResponse;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/areas")
public class AreaController implements IController {

    private final AreaCommandBus commandBus;
    private final AreaQueryBus queryBus;

    public AreaController(AreaCommandBus commandBus, AreaQueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createArea(@RequestBody CreateAreaRequest request) {
        var command = CreateAreaCommand.builder()
                .name(request.getName())
                .maxTable(request.getMaxTable())
                .build();

        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Integer) result.getData())) : handleError(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateArea(@PathVariable("id") Integer id,
                                                     @RequestBody UpdateAreaRequest request) {
        
        var command = UpdateAreaCommand.builder()
                .areaId(id)
                .name(request.getName())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);
    }
    
    @PutMapping("/{id}/max-and-active")
    public ResponseEntity<ApiResponse<?>> updateAreaMaxAndActive(@PathVariable("id") Integer id,
                                                                 @RequestBody UpdateMaxAndActiveRequest request) {
        
        var command = UpdateAreaMaxAndActiveCommand.builder()
                .areaId(id)
                .maxTable(request.getMaxTable())
                .isActive(request.getIsActive())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllArea() {
        var query = DefaultAreaQuery.builder().build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) :
                handleError(result);
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<?>> getAllActiveArea() {
        var query = AreaActiveQuery.builder().build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) :
                handleError(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getAreaById(@PathVariable("id") Integer id) {
        var query = AreaByIdQuery.builder().id(id).build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success(result.getData())) : handleError(result);
    }


}
