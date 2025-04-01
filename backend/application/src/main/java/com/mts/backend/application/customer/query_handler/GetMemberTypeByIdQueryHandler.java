package com.mts.backend.application.customer.query_handler;

import com.mts.backend.application.customer.query.MemberTypeByIdQuery;
import com.mts.backend.application.customer.response.MemberTypeDetailResponse;
import com.mts.backend.domain.customer.jpa.JpaMembershipTypeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

@Service
public class GetMemberTypeByIdQueryHandler implements IQueryHandler<MemberTypeByIdQuery, CommandResult> {
    private final JpaMembershipTypeRepository membershipTypeRepository;
    
    public GetMemberTypeByIdQueryHandler(JpaMembershipTypeRepository membershipTypeRepository) {
        this.membershipTypeRepository = membershipTypeRepository;
    }
    
    @Override
    public CommandResult handle(MemberTypeByIdQuery query) {
        var membershipType = membershipTypeRepository.findById(query.getId())
                .orElseThrow(() -> new RuntimeException("Membership type not found"));

        MemberTypeDetailResponse response = MemberTypeDetailResponse.builder()
                .id(membershipType.getId())
                .name(membershipType.getType().getValue())
                .discountUnit(membershipType.getMemberDiscountValue().getUnit().name())
                .discountValue(membershipType.getMemberDiscountValue().getValue())
                .requiredPoints(membershipType.getRequiredPoint())
                .validUntil(membershipType.getValidUntil().orElse(null))
                .isActive(membershipType.getActive())
                .build();

        return CommandResult.success(response);
    }
                
}
