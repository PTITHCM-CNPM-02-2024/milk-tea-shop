package com.mts.backend.application.customer.query_handler;

import com.mts.backend.application.customer.query.DefaultMemberQuery;
import com.mts.backend.application.customer.response.MemberTypeDetailResponse;
import com.mts.backend.domain.customer.jpa.JpaMembershipTypeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllMemberTypeQueryHandler implements IQueryHandler<DefaultMemberQuery, CommandResult> {

    private final JpaMembershipTypeRepository membershipTypeRepository;

    public GetAllMemberTypeQueryHandler(JpaMembershipTypeRepository membershipTypeRepository) {
        this.membershipTypeRepository = membershipTypeRepository;
    }


    @Override
    public CommandResult handle(DefaultMemberQuery query) {
        Objects.requireNonNull(query, "Default member query is required");

        var memberTypes = membershipTypeRepository.findAll();

        List<MemberTypeDetailResponse> responses = new ArrayList<>();

        memberTypes.forEach(memberType -> {
            responses.add(MemberTypeDetailResponse.builder()
                    .id(memberType.getId())
                    .name(memberType.getType().getValue())
                    .discountUnit(memberType.getMemberDiscountValue().getUnit().name())
                    .discountValue(memberType.getMemberDiscountValue().getValue())
                    .requiredPoints(memberType.getRequiredPoint())
                    .validUntil(memberType.getValidUntil().orElse(null))
                    .description(memberType.getDescription().orElse(null))
                    .isActive(memberType.getActive())
                    .build());
        });

        return CommandResult.success(responses);
    }
}
