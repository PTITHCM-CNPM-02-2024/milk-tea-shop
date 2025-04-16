package com.mts.backend.api.staff.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.staff.request.CreateEmployeeRequest;
import com.mts.backend.api.staff.request.UpdateEmployeeRequest;
import com.mts.backend.application.staff.EmployeeCommandBus;
import com.mts.backend.application.staff.EmployeeQueryBus;
import com.mts.backend.application.staff.command.CreateEmployeeCommand;
import com.mts.backend.application.staff.command.DeleteEmpByIdCommand;
import com.mts.backend.application.staff.command.UpdateEmployeeCommand;
import com.mts.backend.application.staff.query.*;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.domain.account.value_object.Username;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.staff.identifier.EmployeeId;
import com.mts.backend.domain.staff.value_object.Position;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "Employee Controller", description = "Employee")
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController implements IController {
    private final EmployeeCommandBus commandBus;
    private final EmployeeQueryBus queryBus;
    
    public EmployeeController(EmployeeCommandBus commandBus, EmployeeQueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }
    
    @Operation(summary = "Tạo nhân viên mới")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "400", description = "Lỗi dữ liệu đầu vào")
    })
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createEmployee(@Parameter(description = "Thông tin nhân viên", required = true) @RequestBody CreateEmployeeRequest request) {
        var command = CreateEmployeeCommand.builder()
                .email(Email.builder().value(request.getEmail()).build())
                .firstName(FirstName.builder().value(request.getFirstName()).build())
                .lastName(LastName.builder().value(request.getLastName()).build())
                .phone(PhoneNumber.builder().value(request.getPhone()).build())
                .gender(Gender.valueOf(request.getGender()))
                .position(Position.builder().value(request.getPosition()).build())
                .username(Username.builder().value(request.getUsername()).build())
                .password(PasswordHash.builder().value(request.getPassword()).build())
                .roleId(RoleId.of(request.getRoleId()))
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @Operation(summary = "Cập nhật nhân viên")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy nhân viên")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateEmployee(@Parameter(description = "ID nhân viên", required = true) @PathVariable("id") Long id, @Parameter(description = "Thông tin cập nhật", required = true) @RequestBody UpdateEmployeeRequest request) {
        var command = UpdateEmployeeCommand.builder()
                .id(EmployeeId.of(id))
                .email(Email.builder().value(request.getEmail()).build())
                .firstName(FirstName.builder().value(request.getFirstName()).build())
                .lastName(LastName.builder().value(request.getLastName()).build())
                .phone(PhoneNumber.builder().value(request.getPhone()).build())
                .gender(Gender.valueOf(request.getGender()))
                .position(Position.builder().value(request.getPosition()).build())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @Operation(summary = "Lấy nhân viên theo ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy nhân viên")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> getEmployee(@Parameter(description = "ID nhân viên", required = true) @PathVariable("id") Long id) {
        var query = EmployeeByIdQuery.builder().id(EmployeeId.of(id)).build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @Operation(summary = "Lấy danh sách nhân viên")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> getEmployees(
            @Parameter(description = "Trang", required = false) @RequestParam(value = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Kích thước trang", required = false) @RequestParam(value = "size", defaultValue = "10") Integer size) {
        var request = DefaultEmployeeQuery.builder()
                .page(page)
                .size(size)
                .build();
        
        var result = queryBus.dispatch(request);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @Operation(summary = "Lấy bàn order theo nhân viên")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping("/{id}/orders/order-tables")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> getOrderTablesByEmployeeId(@Parameter(description = "ID nhân viên", required = true) @PathVariable("id") Long id,
                                                       @Parameter(description = "Trang", required = false) @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                       @Parameter(description = "Kích thước trang", required = false) @RequestParam(value = "size", defaultValue = "10") Integer size) {
        var query = CheckoutTableByEmpIdQuery.builder().employeeId(EmployeeId.of(id)).page(page).size(size).build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @Operation(summary = "Xóa nhân viên")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy nhân viên")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deleteEmployee(@Parameter(description = "ID nhân viên", required = true) @PathVariable("id") Long id) {
        DeleteEmpByIdCommand command = DeleteEmpByIdCommand.builder()
                .id(EmployeeId.of(id))
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Lấy nhân viên theo tài khoản")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping("/account/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> getEmployeeByAccountId(@Parameter(description = "ID tài khoản", required = true) @PathVariable("id") Long id) {
        var query = GetEmpByAccountIdQuery.builder().accountId(AccountId.of(id)).build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Báo cáo tổng quan đơn hàng theo nhân viên")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping("/{id}/reports/order-overview")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> getOrderOverviewByEmployeeId(@Parameter(description = "ID nhân viên", required = true) @PathVariable("id") Long id,
                                                               @Parameter(description = "Từ ngày", required = false) @RequestParam(value = "fromDate", required = false) String fromDate,
                                                               @Parameter(description = "Đến ngày", required = false) @RequestParam(value = "toDate") String toDate) {
        var query = OrderOverviewByEmpIdQuery.builder().id(EmployeeId.of(id))
                .fromDate(Objects.isNull(fromDate) ? null : LocalDateTime.parse(fromDate))
                .toDate(Objects.isNull(toDate) ? null : LocalDateTime.parse(toDate))
                .build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Báo cáo doanh thu theo nhân viên và thời gian")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping("/{id}/reports/order-revenue")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> getOrderRevenueByTimeAndEmployeeId(@Parameter(description = "ID nhân viên", required = true) @PathVariable("id") Long id,
                                                               @Parameter(description = "Từ ngày", required = true) @RequestParam(value = "fromDate") String fromDate,
                                                               @Parameter(description = "Đến ngày", required = true) @RequestParam(value = "toDate") String toDate) {
        var query = OrderRevenueByTimeAndEmpIdQuery.builder().id(EmployeeId.of(id))
                .fromDate(Objects.isNull(fromDate) ? null : LocalDateTime.parse(fromDate))
                .toDate(Objects.isNull(toDate) ? null : LocalDateTime.parse(toDate))
                .build();
     
        var result = queryBus.dispatch(query);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Lấy tất cả đơn hàng theo nhân viên")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping("/{id}/reports/orders")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> getAllOrderByEmployeeId(@Parameter(description = "ID nhân viên", required = true) @PathVariable("id") Long id,
                                                    @Parameter(description = "Từ ngày", required = false) @RequestParam(value = "fromDate", required = false) String fromDate,
                                                    @Parameter(description = "Đến ngày", required = false) @RequestParam(value = "toDate", required = false) String toDate,
                                                    @Parameter(description = "Trang", required = false) @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                    @Parameter(description = "Kích thước trang", required = false) @RequestParam(value = "size", defaultValue = "10") Integer size) {
        var query = OrderByEmpIdQuery.builder().employeeId(EmployeeId.of(id))
                .fromDate(LocalDateTime.parse(fromDate))
                .toDate(LocalDateTime.parse(toDate))
                .page(page)
                .size(size)
                .build();
        
        var result = queryBus.dispatch(query);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
