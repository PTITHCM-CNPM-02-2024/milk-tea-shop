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
import com.mts.backend.application.customer.query_handler.GetMemberTypeByIdQueryHandler;
import com.mts.backend.application.customer.response.MemberTypeDetailResponse;
import com.mts.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    public ResponseEntity<ApiResponse<Integer>> createMembership(@RequestBody CreateMembershipTypeRequest request) {
        var command = CreateMembershipCommand.builder().build();
        
        command.setName(request.getName());
        command.setDescription(request.getDescription());
        command.setDiscountUnit(request.getDiscountUnit());
        command.setDiscountValue(BigDecimal.valueOf(request.getDiscountValue()));
        command.setRequiredPoints(request.getRequiredPoint());
        command.setValidUntil(LocalDateTime.parse(request.getValidUntil()));
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Integer) result.getData())) : handleError(result);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Integer>> updateMembership(@PathVariable("id") Integer id, @RequestBody UpdateMembershipTypeRequest request){
        var command = UpdateMemberCommand.builder()
                .memberId(id)
                .name(request.getName())
                .description(request.getDescription())
                .discountUnit(request.getDiscountUnit())
                .discountValue(BigDecimal.valueOf(request.getDiscountValue()))
                .requiredPoints(request.getRequiredPoint())
                .validUntil(LocalDateTime.parse(request.getValidUntil()))
                .isActive(request.isActive())
                .build();
        
        var result = commandBus.dispatch(command);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((Integer) result.getData())) : handleError(result);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getMembership(@PathVariable("id") Integer id){
        var query = MemberTypeByIdQuery.builder()
                .id(id).build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success( (MemberTypeDetailResponse) result.getData())) : handleError(result);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getMemberships(){
        var query = DefaultMemberQuery.builder().build();
        
        var result = queryBus.dispatch(query);
        
        return result.isSuccess() ? ResponseEntity.ok(ApiResponse.success((List<MemberTypeDetailResponse>) result.getData())) : handleError(result);
    }
}
