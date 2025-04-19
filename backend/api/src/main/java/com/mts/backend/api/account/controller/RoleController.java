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
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.value_object.RoleName;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Role Controller", description = "Role")
@RestController
@RequestMapping("/api/v1/roles")
public class RoleController implements IController {

    private final RoleCommandBus roleCommandBus;
    private final RoleQueryBus roleQueryBus;

    public RoleController(RoleCommandBus roleCommandBus, RoleQueryBus roleQueryBus) {
        this.roleCommandBus = roleCommandBus;
        this.roleQueryBus = roleQueryBus;
    }

    @Operation(summary = "Tạo vai trò mới")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "400", description = "Lỗi dữ liệu đầu vào")
    })
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createRole(@Parameter(description = "Thông tin vai trò", required = true) @RequestBody CreateRoleRequest request) {
        var command = CreateRoleCommand.builder()
                .name(RoleName.of(request.getName()))
                .description(request.getDescription())
                .build();
        var result = roleCommandBus.dispatch(command);
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Lấy danh sách vai trò")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getAllRoles(@Parameter(description = "Trang", required = false) @RequestParam(value = "page", defaultValue = "0") Integer page,
                                         @Parameter(description = "Kích thước trang", required = false) @RequestParam(value = "size", defaultValue = "10") Integer size) {
        var query = DefaultRoleQuery.builder().page(page).size(size).build();
        var result = roleQueryBus.dispatch(query);
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Cập nhật vai trò")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy vai trò")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateRole(@Parameter(description = "ID vai trò", required = true) @PathVariable("id") Integer id, @Parameter(description = "Thông tin cập nhật", required = true) @RequestBody UpdateRoleRequest request) {
        var command = UpdateRoleCommand.builder()
                .id(RoleId.of(id))
                .roleName(RoleName.of(request.getName()))
                .description(request.getDescription())
                .build();
        var result = roleCommandBus.dispatch(command);
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Lấy vai trò theo ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy vai trò")
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getRoleById(@Parameter(description = "ID vai trò", required = true) @PathVariable("id") Integer id) {
        var query = RoleByIdQuery.builder().id(RoleId.of(id)).build();
        var result = roleQueryBus.dispatch(query);
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Xóa vai trò")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy vai trò")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deleteRoleById(@Parameter(description = "ID vai trò", required = true) @PathVariable("id") Integer id) {
        var command = DeleteRoleByIdCommand.builder().roleId(RoleId.of(id)).build();
        var result = roleCommandBus.dispatch(command);
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
