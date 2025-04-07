package com.mts.backend.api.customer.controller;

import com.mts.backend.api.common.IController;
import com.mts.backend.api.customer.request.CreateMembershipTypeRequest;
import com.mts.backend.api.customer.request.UpdateMembershipTypeRequest;
import com.mts.backend.application.customer.MembershipTypeCommandBus;
import com.mts.backend.application.customer.MembershipTypeQueryBus;
import com.mts.backend.application.customer.command.CreateMembershipCommand;
import com.mts.backend.application.customer.command.UpdateMemberCommand;
import com.mts.backend.application.customer.query.DefaultMemberQuery;
import com.mts.backend.application.customer.query.MemberTypeByIdQuery;
import com.mts.backend.application.customer.response.MemberTypeDetailResponse;
import com.mts.backend.domain.common.value_object.DiscountUnit;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.value_object.MemberTypeName;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/memberships")
public class MembershipController implements IController {
    private final MembershipTypeCommandBus commandBus;
    private final MembershipTypeQueryBus queryBus;

    public MembershipController(MembershipTypeCommandBus commandBus, MembershipTypeQueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }
    
    @PostMapping
    public ResponseEntity<?> createMembership(@RequestBody CreateMembershipTypeRequest request) {
        var command = CreateMembershipCommand.
                builder()
                .name(MemberTypeName.builder().value(request.getName()).build())
                .description(request.getDescription())
                .discountUnit(DiscountUnit.valueOf(request.getDiscountUnit()))
                .discountValue(BigDecimal.valueOf(request.getDiscountValue()))
                .requiredPoints(request.getRequiredPoint())
                .validUntil(request.getValidUntil() == null ? null : LocalDateTime.parse(request.getValidUntil()))
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMembership(@PathVariable("id") Integer id, @RequestBody UpdateMembershipTypeRequest request){
        var command = UpdateMemberCommand.builder()
                .memberId(MembershipTypeId.of(id))
                .name(MemberTypeName.builder().value(request.getName()).build())
                .description(request.getDescription())
                .discountUnit(DiscountUnit.valueOf(request.getDiscountUnit()))
                .discountValue(BigDecimal.valueOf(request.getDiscountValue()))
                .requiredPoints(request.getRequiredPoint())
                .validUntil(LocalDateTime.parse(request.getValidUntil()))
                .active(request.isActive())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getMembership(@PathVariable("id") Integer id){
        var query = MemberTypeByIdQuery.builder()
                .id(MembershipTypeId.of(id))
                .build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
    
    @GetMapping
    public ResponseEntity<?> getMemberships(@RequestParam(value = "page", defaultValue = "0") int page,
                                            @RequestParam(value = "size", defaultValue = "10") int size){
        var query = DefaultMemberQuery.builder().
                page(page)
                .size(size)
                .build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(result.getData()) : handleError(result);
    }
}
