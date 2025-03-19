package com.mts.backend.application.promotion.query_handler;

import com.mts.backend.application.promotion.query.DiscountByCouponQuery;
import com.mts.backend.application.promotion.response.DiscountDetailResponse;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.repository.IDiscountRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetDiscountByCouponQueryHandler implements IQueryHandler<DiscountByCouponQuery, CommandResult> {

    private final IDiscountRepository discountRepository;

    public GetDiscountByCouponQueryHandler(IDiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    /**
     * @param query
     * @return
     */
    @Override
    public CommandResult handle(DiscountByCouponQuery query) {
        Objects.requireNonNull(query, "Discount by coupon query is required");

        var discount = discountRepository.findByCouponId(CouponId.of(query.getCouponId())).orElseThrow(() -> new NotFoundException(
                "Không tìm thấy mã giảm giá"));

        var response = DiscountDetailResponse.builder()
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
                .build();
        
        return CommandResult.success(response);
    }
}
