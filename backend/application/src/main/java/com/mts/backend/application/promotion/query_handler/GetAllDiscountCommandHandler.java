package com.mts.backend.application.promotion.query_handler;

import com.mts.backend.application.promotion.query.DefaultDiscountQuery;
import com.mts.backend.application.promotion.response.DiscountDetailResponse;
import com.mts.backend.domain.promotion.jpa.JpaDiscountRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllDiscountCommandHandler implements IQueryHandler<DefaultDiscountQuery, CommandResult> {
    private final JpaDiscountRepository discountRepository;

    public GetAllDiscountCommandHandler(JpaDiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    @Override
    @Transactional
    public CommandResult handle(DefaultDiscountQuery query) {
        Objects.requireNonNull(query, "Default discount query is required");

        var discounts = discountRepository.findAll(Pageable.ofSize(query.getSize()).withPage(query.getPage()));

        Page<DiscountDetailResponse> responses = discounts.map(discount -> {
            return DiscountDetailResponse.builder()
                    .id(discount.getId())
                    .name(discount.getName().getValue())
                    .description(discount.getDescription())
                    .couponId(discount.getCouponEntity().getId())
                    .discountValue(discount.getPromotionDiscountValue().getValue())
                    .discountUnit(discount.getPromotionDiscountValue().getUnit().name())
                    .maxDiscountAmount(discount.getPromotionDiscountValue().getMaxDiscountAmount().getValue())
                    .minimumOrderValue(discount.getMinRequiredOrderValue().getValue())
                    .minimumRequiredProduct(discount.getMinRequiredProduct().orElse(null))
                    .validFrom(discount.getValidFrom().orElse(null))
                    .validUntil(discount.getValidUntil())
                    .maxUsage(discount.getMaxUse().orElse(null))
                    .maxUsagePerCustomer(discount.getMaxUsesPerCustomer().orElse(null))
                    .isActive(discount.getActive())
                    .build();
        });
        

        return CommandResult.success(responses);
    }
}
