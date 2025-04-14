package com.mts.backend.api.account.controller;

import com.mts.backend.api.account.request.CreateRoleRequest;
import com.mts.backend.api.account.request.UpdateRoleRequest;
import com.mts.backend.api.common.IController;
import com.mts.backend.application.account.RoleCommandBus;
import com.mts.backend.application.account.RoleQueryBus;
import com.mts.backend.application.account.command.CreateRoleCommand;
import com.mts.backend.application.account.command.DeleteRoleByIdCommand;
import com.mts.backend.application.account.command.UpdateRoleCommand;
import com.mts.backend.application.account.query.DefaultRoleQuery;
import com.mts.backend.application.account.query.RoleByIdQuery;
import com.mts.backend.application.account.response.RoleDetailResponse;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.value_object.RoleName;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController implements IController {

    private final RoleCommandBus roleCommandBus;
    private final RoleQueryBus roleQueryBus;

    public RoleController(RoleCommandBus roleCommandBus, RoleQueryBus roleQueryBus) {
        this.roleCommandBus = roleCommandBus;
        this.roleQueryBus = roleQueryBus;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<?> createRole(@RequestBody CreateRoleRequest request) {
        var command = CreateRoleCommand.builder()
                .name(RoleName.builder().value(request.getName()).build())
                .description(request.getDescription())
                .build();
        var result = roleCommandBus.dispatch(command);
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<?> getAllRoles(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size) {
        var query = DefaultRoleQuery.builder().page(page).size(size).build();
        var result = roleQueryBus.dispatch(query);
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<?> updateRole(@PathVariable("id") Integer id, @RequestBody UpdateRoleRequest request) {
        var command = UpdateRoleCommand.builder()
                .id(RoleId.of(id))
                .roleName(RoleName.builder().value(request.getName()).build())
                .description(request.getDescription())
                .build();
        var result = roleCommandBus.dispatch(command);
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getRoleById(@PathVariable("id") Integer id) {
        var query = RoleByIdQuery.builder().id(RoleId.of(id)).build();
        var result = roleQueryBus.dispatch(query);
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<?> deleteRoleById(@PathVariable("id") Integer id) {
        var command = DeleteRoleByIdCommand.builder().roleId(RoleId.of(id)).build();
        var result = roleCommandBus.dispatch(command);
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
