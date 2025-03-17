package com.mts.backend.application.customer.query_handler;

import com.mts.backend.application.customer.query.MemberTypeByIdQuery;
import com.mts.backend.application.customer.response.MemberTypeDetailResponse;
import com.mts.backend.domain.customer.MembershipType;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.repository.IMembershipTypeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

@Service
public class GetMemberTypeByIdQueryHandler implements IQueryHandler<MemberTypeByIdQuery, CommandResult> {
    private final IMembershipTypeRepository membershipTypeRepository;
    
    public GetMemberTypeByIdQueryHandler(IMembershipTypeRepository membershipTypeRepository) {
        this.membershipTypeRepository = membershipTypeRepository;
    }
    
    @Override
    public CommandResult handle(MemberTypeByIdQuery query) {
        var membershipType = membershipTypeRepository.findById(MembershipTypeId.of(query.getId()))
                .orElseThrow(() -> new RuntimeException("Membership type not found"));

        MemberTypeDetailResponse response = MemberTypeDetailResponse.builder()
                .id(membershipType.getId().getValue())
                .name(membershipType.getName().getValue())
                .discountUnit(membershipType.getDiscountValue().getUnit().name())
                .discountValue(membershipType.getDiscountValue().getValue())
                .requiredPoints(membershipType.getRequiredPoint())
                .validUntil(membershipType.getValidUntil())
                .build();

        return CommandResult.success(response);
    }
                
}
