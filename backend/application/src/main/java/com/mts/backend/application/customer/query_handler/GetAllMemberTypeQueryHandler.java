package com.mts.backend.application.customer.query_handler;

import com.mts.backend.application.customer.query.DefaultMemberQuery;
import com.mts.backend.application.customer.response.MemberTypeDetailResponse;
import com.mts.backend.domain.customer.repository.IMembershipTypeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllMemberTypeQueryHandler implements IQueryHandler<DefaultMemberQuery, CommandResult> {

    private final IMembershipTypeRepository membershipTypeRepository;

    public GetAllMemberTypeQueryHandler(IMembershipTypeRepository membershipTypeRepository) {
        this.membershipTypeRepository = membershipTypeRepository;
    }


    @Override
    public CommandResult handle(DefaultMemberQuery query) {
        Objects.requireNonNull(query, "Default member query is required");

        var memberTypes = membershipTypeRepository.findAll();

        List<MemberTypeDetailResponse> responses = new ArrayList<>();

        memberTypes.forEach(memberType -> {
            responses.add(MemberTypeDetailResponse.builder()
                    .id(memberType.getId().getValue())
                    .name(memberType.getName().getValue())
                    .discountUnit(memberType.getDiscountValue().getUnit().name())
                    .discountValue(memberType.getDiscountValue().getValue())
                    .requiredPoints(memberType.getRequiredPoint())
                    .validUntil(memberType.getValidUntil())
                    .isActive(memberType.isActive())
                    .build());
        });

        return CommandResult.success(responses);
    }
}
