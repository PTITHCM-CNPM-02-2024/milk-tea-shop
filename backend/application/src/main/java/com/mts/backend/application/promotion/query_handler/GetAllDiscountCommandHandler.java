package com.mts.backend.application.promotion.query_handler;

import com.mts.backend.application.promotion.query.DefaultDiscountQuery;
import com.mts.backend.application.promotion.response.DiscountDetailResponse;
import com.mts.backend.domain.promotion.repository.IDiscountRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllDiscountCommandHandler implements IQueryHandler<DefaultDiscountQuery, CommandResult> {
    private final IDiscountRepository discountRepository;

    public GetAllDiscountCommandHandler(IDiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    @Override
    public CommandResult handle(DefaultDiscountQuery query) {
        Objects.requireNonNull(query, "Default discount query is required");

        var discounts = discountRepository.findAll();

        List<DiscountDetailResponse> responses = new ArrayList<>();

        discounts.forEach(discount -> {
            responses.add(DiscountDetailResponse.builder()
                    .id(discount.getId().getValue())
                    .name(discount.getName().getValue())
                    .description(discount.getDescription())
                    .couponId(discount.getCouponId().getValue())
                    .discountValue(discount.getDiscountValue().getValue())
                    .discountUnit(discount.getDiscountValue().getUnit().toString())
                    .maxDiscountAmount(discount.getMaxDiscountAmount().getAmount())
                    .minimumOrderValue(discount.getMinRequiredOrderValue().getAmount())
                    .minimumRequiredProduct(discount.getMinRequiredProduct().orElse(null))
                    .validFrom(discount.getValidFrom().orElse(null))
                    .validUntil(discount.getValidUntil())
                    .maxUsage(discount.getMaxUsage().orElse(null))
                    .maxUsagePerCustomer(discount.getMaxUsagePerCustomer().orElse(null))
                    .isActive(discount.isActive())
                    .build());
        });

        return CommandResult.success(responses);
    }
}
