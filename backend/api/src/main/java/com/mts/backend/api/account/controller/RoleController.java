package com.mts.backend.api.account.controller;

import com.mts.backend.api.account.request.CreateRoleRequest;
import com.mts.backend.api.account.request.UpdateRoleRequest;
import com.mts.backend.api.common.IController;
import com.mts.backend.application.account.RoleCommandBus;
import com.mts.backend.application.account.RoleQueryBus;
import com.mts.backend.application.account.command.CreateRoleCommand;
import com.mts.backend.application.account.command.UpdateRoleCommand;
import com.mts.backend.application.account.query.DefaultRoleQuery;
import com.mts.backend.application.account.query.RoleByIdQuery;
import com.mts.backend.application.account.response.RoleDetailResponse;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/v1/roles")
public class RoleController implements IController{
    
    private final RoleCommandBus roleCommandBus;
    private final RoleQueryBus roleQueryBus;
    
    public RoleController(RoleCommandBus roleCommandBus, RoleQueryBus roleQueryBus) {
        this.roleCommandBus = roleCommandBus;
        this.roleQueryBus = roleQueryBus;
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Integer>> createRole(@RequestBody CreateRoleRequest request) {
        var command = CreateRoleCommand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        var result = roleCommandBus.dispatch(command);
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Integer) result.getData())) : handleError(result);
    }
    
    @GetMapping
    public ResponseEntity<List<RoleDetailResponse>> getAllRoles() {
        var query = DefaultRoleQuery.builder().build();
        var result = roleQueryBus.dispatch(query);
        return result.isSuccess() ? ResponseEntity.ok((List<RoleDetailResponse>) result.getData()) : handleError(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Integer>> updateRole(@PathVariable("id") Integer id, @RequestBody UpdateRoleRequest request) {
        var command = UpdateRoleCommand.builder()
                .id(id)
                .roleName(request.getName())
                .description(request.getDescription())
                .build();
        var result = roleCommandBus.dispatch(command);
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Integer) result.getData())) : handleError(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RoleDetailResponse> getRoleById(@PathVariable("id") Integer id) {
        var query = RoleByIdQuery.builder().id(id).build();
        var result = roleQueryBus.dispatch(query);
        return result.isSuccess() ? ResponseEntity.ok((RoleDetailResponse) result.getData()) : handleError(result);
    }
}
