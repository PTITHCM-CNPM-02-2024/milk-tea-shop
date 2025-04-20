package com.mts.backend.api.customer.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.customer.request.CreateMembershipTypeRequest;
import com.mts.backend.api.customer.request.UpdateMembershipTypeRequest;
import com.mts.backend.application.customer.MembershipTypeCommandBus;
import com.mts.backend.application.customer.MembershipTypeQueryBus;
import com.mts.backend.application.customer.command.CreateMembershipCommand;
import com.mts.backend.application.customer.command.DeleteMemberByIdCommand;
import com.mts.backend.application.customer.command.UpdateMemberCommand;
import com.mts.backend.application.customer.query.DefaultMemberQuery;
import com.mts.backend.application.customer.query.MemberTypeByIdQuery;
import com.mts.backend.domain.common.value_object.DiscountUnit;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.value_object.MemberTypeName;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "Membership Controller", description = "Membership")
@RestController
@RequestMapping("/api/v1/memberships")
public class MembershipController implements IController {
    private final MembershipTypeCommandBus commandBus;
    private final MembershipTypeQueryBus queryBus;

    public MembershipController(MembershipTypeCommandBus commandBus, MembershipTypeQueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }
    
    @Operation(summary = "Tạo hạng thành viên mới")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "400", description = "Lỗi dữ liệu đầu vào")
    })
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createMembership(@Parameter(description = "Thông tin hạng thành viên", required = true) @RequestBody CreateMembershipTypeRequest request) {
        var command = CreateMembershipCommand.
                builder()
                .name(MemberTypeName.of(request.getName()))
                .description(request.getDescription())
                .discountUnit(DiscountUnit.valueOf(request.getDiscountUnit()))
                .discountValue(request.getDiscountValue())
                .requiredPoints(request.getRequiredPoint())
                .validUntil(request.getValidUntil() == null ? null : ZonedDateTime.parse(request.getValidUntil()).toLocalDateTime())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @Operation(summary = "Cập nhật hạng thành viên")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy hạng thành viên")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> updateMembership(@Parameter(description = "ID hạng thành viên", required = true) @PathVariable("id") Integer id, @Parameter(description = "Thông tin cập nhật", required = true) @RequestBody UpdateMembershipTypeRequest request){
        var command = UpdateMemberCommand.builder()
                .memberId(MembershipTypeId.of(id))
                .name(MemberTypeName.of(request.getName()))
                .description(request.getDescription())
                .discountUnit(DiscountUnit.valueOf(request.getDiscountUnit()))
                .discountValue(BigDecimal.valueOf(request.getDiscountValue()))
                .requiredPoints(request.getRequiredPoint())
                .validUntil(request.getValidUntil() == null ? null : ZonedDateTime.parse(request.getValidUntil()).toLocalDateTime())
                .active(request.isActive())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @Operation(summary = "Lấy hạng thành viên theo ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy hạng thành viên")
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMembership(@Parameter(description = "ID hạng thành viên", required = true) @PathVariable("id") Integer id){
        var query = MemberTypeByIdQuery.builder()
                .id(MembershipTypeId.of(id))
                .build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @Operation(summary = "Lấy danh sách hạng thành viên")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công")
    })
    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getMemberships(@Parameter(description = "Trang", required = false) @RequestParam(value = "page", defaultValue = "0") Integer page,
                                            @Parameter(description = "Kích thước trang", required = false) @RequestParam(value = "size", defaultValue = "10") Integer size){
        var query = DefaultMemberQuery.builder()
                .build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }

    @Operation(summary = "Xóa hạng thành viên")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Thành công"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy hạng thành viên")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> deleteMembership(@Parameter(description = "ID hạng thành viên", required = true) @PathVariable("id") Integer id) {
        DeleteMemberByIdCommand command = DeleteMemberByIdCommand.builder()
                .membershipTypeId(MembershipTypeId.of(id))
                .build();

        var result = commandBus.dispatch(command);

        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
