package com.mts.backend.api.store.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.store.request.CreateAreaRequest;
import com.mts.backend.api.store.request.UpdateAreaRequest;
import com.mts.backend.api.store.request.UpdateMaxAndActiveRequest;
import com.mts.backend.application.store.AreaCommandBus;
import com.mts.backend.application.store.AreaQueryBus;
import com.mts.backend.application.store.command.CreateAreaCommand;
import com.mts.backend.application.store.command.DeleteAreaByIdCommand;
import com.mts.backend.application.store.command.UpdateAreaCommand;
import com.mts.backend.application.store.command.UpdateAreaMaxAndActiveCommand;
import com.mts.backend.application.store.query.AreaActiveQuery;
import com.mts.backend.application.store.query.AreaByIdQuery;
import com.mts.backend.application.store.query.DefaultAreaQuery;
import com.mts.backend.application.store.query.ServiceTableByAreaIdQuery;
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
                .maxTable(request.getMaxTable().map(max -> MaxTable.builder().value(max).build()).orElse(null))
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
                .description(request.getDescription().orElse(null))
                .active(request.getIsActive())
                .maxTable(request.getMaxTable().map(max -> MaxTable.builder().value(max).build()).orElse(null))
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    
    @GetMapping
    public ResponseEntity<?> getAllArea(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        var query = DefaultAreaQuery.builder()
                .page(page)
                .size(size)
                .build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) :
                handleError(result);
    }
    
    @GetMapping("/active/{active}")
    public ResponseEntity<?> getAllAreaActive(@PathVariable("active") Boolean active) {
        var query = AreaActiveQuery.builder().active(active).build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getAreaById(@PathVariable("id") Integer id) {
        var query = AreaByIdQuery.builder().id(AreaId.of(id)).build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    
    @GetMapping("/{id}/tables")
    public ResponseEntity<?> getAreaTables(@PathVariable("id") Integer id,
                                           @RequestParam(value = "page", defaultValue = "0") Integer page,
                                           @RequestParam(value = "size", defaultValue = "10") Integer size) {
        
        var query = ServiceTableByAreaIdQuery.builder()
                .areaId(AreaId.of(id))
                .page(page)
                .size(size)
                .build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArea(@PathVariable("id") Integer id) {
        DeleteAreaByIdCommand command = DeleteAreaByIdCommand.builder()
                .areaId(AreaId.of(id))
                .build();

        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
