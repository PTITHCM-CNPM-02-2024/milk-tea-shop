package com.mts.backend.api.customer.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.customer.request.CreateCustomerRequest;
import com.mts.backend.api.customer.request.UpdateCustomerRequest;
import com.mts.backend.application.customer.CustomerCommandBus;
import com.mts.backend.application.customer.CustomerQueryBus;
import com.mts.backend.application.customer.command.CreateCustomerCommand;
import com.mts.backend.application.customer.command.DeleteCusByIdCommand;
import com.mts.backend.application.customer.command.UpdateCustomerCommand;
import com.mts.backend.application.customer.command.UpdateMemberForCustomer;
import com.mts.backend.application.customer.query.CustomerByIdQuery;
import com.mts.backend.application.customer.query.CustomerByPhoneQuery;
import com.mts.backend.application.customer.query.DefaultCustomerQuery;
import com.mts.backend.application.customer.query.GetCusByAccountIdQuery;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.domain.account.value_object.Username;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Tag(name = "Customer Controller", description = "Customer")
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController implements IController {
    private final CustomerCommandBus customerCommandBus;
    private final CustomerQueryBus customerQueryBus;

    public CustomerController(CustomerCommandBus customerCommandBus, CustomerQueryBus customerQueryBus) {
        this.customerCommandBus = customerCommandBus;
        this.customerQueryBus = customerQueryBus;
    }

    @Operation(summary = "Tạo khách hàng mới")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "400", description = "Lỗi dữ liệu đầu vào")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> createCustomer(@Parameter(description = "Thông tin khách hàng", required = true) @RequestBody CreateCustomerRequest request) {
        CreateCustomerCommand command = CreateCustomerCommand.builder()
                .firstName(Objects.isNull(request.getFirstName()) ? null : FirstName.of(request.getFirstName()))
                .lastName(Objects.isNull(request.getLastName()) ? null : LastName.of(request.getLastName()))
                .email(Objects.isNull(request.getEmail()) ? null : Email.of(request.getEmail()))
                .gender(Objects.isNull(request.getGender()) ? null : Gender.valueOf(request.getGender()))
                .phone(PhoneNumber.of(request.getPhone()))
                .membershipId(Objects.isNull(request.getMemberId()) ? null :
                        MembershipTypeId.of(request.getMemberId()))
                .build();

        if (!Objects.isNull(request.getUsername()) && !Objects.isNull(request.getPassword())) {
            command.setUsername(Username.of(request.getUsername()));
            command.setPasswordHash(PasswordHash.of(request.getPassword()));
            command.setRoleId(RoleId.of(request.getRoleId()));
        }


        var result = customerCommandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);

    }

    @Operation(summary = "Cập nhật khách hàng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF', 'CUSTOMER')")
    public ResponseEntity<?> updateCustomer(@Parameter(description = "ID khách hàng", required = true) @PathVariable("id") Long id, @Parameter(description = "Thông tin cập nhật", required = true) @RequestBody UpdateCustomerRequest request) {
        UpdateCustomerCommand command = UpdateCustomerCommand.builder()
                .id(CustomerId.of(id))
                .email(Objects.isNull(request.getEmail()) ? null : Email.of(request.getEmail()))
                .firstName(Objects.isNull(request.getFirstName()) ? null :
                        FirstName.of(request.getFirstName()))
                .lastName(Objects.isNull(request.getLastName()) ? null :
                        LastName.of(request.getLastName()))
                .phone(PhoneNumber.of(request.getPhone()))
                .gender(Objects.isNull(request.getGender()) ? null : Gender.valueOf(request.getGender()))
                .build();

        var result = customerCommandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Cập nhật hạng thành viên cho khách hàng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @PutMapping("/{id}/membership")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateMembership(@Parameter(description = "ID khách hàng", required = true) @PathVariable("id") Long id, @Parameter(description = "ID hạng thành viên", required = true) @RequestParam("value") Integer value) {
        UpdateMemberForCustomer command = UpdateMemberForCustomer.builder()
                .customerId(CustomerId.of(id))
                .memberId(MembershipTypeId.of(value))
                .build();

        var result = customerCommandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Lấy khách hàng theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng")
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCustomer(@Parameter(description = "ID khách hàng", required = true) @PathVariable("id") Long id) {
        var query = CustomerByIdQuery.builder().
                id(CustomerId.of(id))
                .build();

        var result = customerQueryBus.dispatch(query);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Lấy danh sách khách hàng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> getCustomers(@Parameter(description = "Trang", required = false) @RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @Parameter(description = "Kích thước trang", required = false) @RequestParam(value = "size", defaultValue = "10") Integer size) {
        var request = DefaultCustomerQuery.builder().
                page(page)
                .size(size)
                .build();

        var result = customerQueryBus.dispatch(request);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Tìm khách hàng theo số điện thoại")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping("/search/phone")
    @PreAuthorize("hasAnyRole('MANAGER', 'STAFF')")
    public ResponseEntity<?> getCustomerByPhone(@Parameter(description = "Số điện thoại", required = true) @RequestParam("phone") String phone) {
        var request = CustomerByPhoneQuery.builder().phone(PhoneNumber.of(phone)).build();

        var result = customerQueryBus.dispatch(request);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Xóa khách hàng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deleteCustomer(@Parameter(description = "ID khách hàng", required = true) @PathVariable("id") Long id) {
        DeleteCusByIdCommand command = DeleteCusByIdCommand.builder()
                .customerId(CustomerId.of(id))
                .build();

        var result = customerCommandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Lấy khách hàng theo tài khoản")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping("/account/{id}")
    @PreAuthorize("(hasAnyRole('MANAGER', 'STAFF') ) or @authentication.principal.id == #id")
    public ResponseEntity<?> getAccount(@Parameter(description = "ID tài khoản", required = true) @PathVariable("id") Long id) {
        var query = GetCusByAccountIdQuery.builder()
                .accountId(AccountId.of(id))
                .build();

        var result = customerQueryBus.dispatch(query);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
