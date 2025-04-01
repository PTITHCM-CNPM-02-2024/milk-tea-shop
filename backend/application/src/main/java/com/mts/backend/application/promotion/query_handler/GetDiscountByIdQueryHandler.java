package com.mts.backend.application.promotion.query_handler;

import com.mts.backend.application.promotion.query.DiscountByIdQuery;
import com.mts.backend.application.promotion.response.DiscountDetailResponse;
import com.mts.backend.domain.promotion.jpa.JpaDiscountRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

@Service
public class GetDiscountByIdQueryHandler implements IQueryHandler<DiscountByIdQuery, CommandResult> {

    private final JpaDiscountRepository discountRepository;

    public GetDiscountByIdQueryHandler(JpaDiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    /**
     * @param query
     * @return
     */
    @Override
    public CommandResult handle(DiscountByIdQuery query) {

        var discount =
                discountRepository.findById(query.getId().getValue()).orElseThrow(() -> new IllegalArgumentException("Không " +
                        "tìm thấy mã giảm giá"));


        var response = DiscountDetailResponse.builder()
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
        return CommandResult.success(response);

    }
}
