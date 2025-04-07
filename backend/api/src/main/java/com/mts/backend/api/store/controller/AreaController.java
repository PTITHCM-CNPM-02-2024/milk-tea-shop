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
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.value_object.AreaName;
import com.mts.backend.domain.store.value_object.MaxTable;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> createArea(@RequestBody CreateAreaRequest request) {
        var command = CreateAreaCommand.builder()
                .name(AreaName.builder().value(request.getName()).build())
                .isActive(request.getIsActive())
                .maxTable(request.getMaxTable() != null ? MaxTable.builder().value(request.getMaxTable()).build() : null)
                .build();

        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateArea(@PathVariable("id") Integer id,
                                                     @RequestBody UpdateAreaRequest request) {
        
        var command = UpdateAreaCommand.builder()
                .areaId(AreaId.of(id))
                .name(AreaName.builder().value(request.getName()).build())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @PutMapping("/{id}/max-and-active")
    public ResponseEntity<?> updateAreaMaxAndActive(@PathVariable("id") Integer id,
                                                                 @RequestBody UpdateMaxAndActiveRequest request) {
        
        var command = UpdateAreaMaxAndActiveCommand.builder()
                .areaId(AreaId.of(id))
                .maxTable(request.getMaxTable() != null ? MaxTable.builder().value(request.getMaxTable()).build() : null)
                .isActive(request.getIsActive())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllArea(@RequestParam(value = "active", required = false) Boolean active) {
        var query = DefaultAreaQuery.builder().active(active).build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) :
                handleError(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getAreaById(@PathVariable("id") Integer id) {
        var query = AreaByIdQuery.builder().id(AreaId.of(id)).build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
