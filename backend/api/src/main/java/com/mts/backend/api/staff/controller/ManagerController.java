package com.mts.backend.api.staff.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.staff.request.ManagerRequest;
import com.mts.backend.api.staff.request.UpdateManagerRequest;
import com.mts.backend.application.staff.ManagerCommandBus;
import com.mts.backend.application.staff.ManagerQueryBus;
import com.mts.backend.application.staff.command.CreateManagerCommand;
import com.mts.backend.application.staff.command.UpdateManagerCommand;
import com.mts.backend.application.staff.query.DefaultManagerQuery;
import com.mts.backend.application.staff.query.ManagerByIdQuery;
import com.mts.backend.application.staff.response.ManagerDetailResponse;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/managers")
public class ManagerController implements IController {
    private final ManagerCommandBus commandBus;
    private final ManagerQueryBus queryBus;
    
    public ManagerController(ManagerCommandBus commandBus, ManagerQueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createManager(@RequestBody ManagerRequest request) {
        var command = CreateManagerCommand.builder().build();
        
        command.setEmail(request.getEmail());
        command.setFirstName(request.getFirstName());
        command.setLastName(request.getLastName());
        command.setGender(request.getGender());
        command.setPhone(request.getPhone());
        command.setAccountId(request.getAccountId());
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Long) result.getData())) : handleError(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Long>> updateManager(@PathVariable("id") Long id, @RequestBody UpdateManagerRequest request) {
        var command = UpdateManagerCommand.builder().build();
        
        command.setId(id);
        command.setEmail(request.getEmail());
        command.setFirstName(request.getFirstName());
        command.setLastName(request.getLastName());
        command.setGender(request.getGender());
        command.setPhone(request.getPhone());
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Long) result.getData())) : handleError(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getManager(@PathVariable("id") Long id) {
        var query = ManagerByIdQuery.builder().build();
        
        query.setId(id);
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((ManagerDetailResponse)result.getData())) : handleError(result);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getManagers() {
        var request = DefaultManagerQuery.builder().build();
        
        var result = queryBus.dispatch(request);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((List<ManagerDetailResponse>)result.getData())) : handleError(result);
    }
}
