package com.mts.backend.application.promotion.query_handler;

import com.mts.backend.application.promotion.query.DiscountByCouponQuery;
import com.mts.backend.application.promotion.response.DiscountDetailResponse;
import com.mts.backend.domain.promotion.jpa.JpaDiscountRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetDiscountByCouponQueryHandler implements IQueryHandler<DiscountByCouponQuery, CommandResult> {

    private final JpaDiscountRepository discountRepository;

    public GetDiscountByCouponQueryHandler(JpaDiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    /**
     * @param query
     * @return
     */
    @Override
    public CommandResult handle(DiscountByCouponQuery query) {
        Objects.requireNonNull(query, "Discount by coupon query is required");

        var discount =
                discountRepository.findByCouponEntity_Coupon(query.getCode().getValue()).orElseThrow(() -> new NotFoundException(
                "Không tìm thấy mã giảm giá"));

        var response = DiscountDetailResponse.builder()
                .id(discount.getId())
                .name(discount.getName().getValue())
                .description(discount.getDescription())
                .couponId(discount.getCoupon().getId())
                .couponCode(discount.getCoupon().getCoupon().getValue())
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
