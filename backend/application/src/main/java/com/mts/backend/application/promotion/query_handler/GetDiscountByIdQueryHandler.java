package com.mts.backend.application.promotion.query_handler;

import com.mts.backend.application.promotion.query.DiscountByIdQuery;
import com.mts.backend.application.promotion.response.DiscountDetailResponse;
import com.mts.backend.domain.promotion.identifier.DiscountId;
import com.mts.backend.domain.promotion.repository.IDiscountRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

@Service
public class GetDiscountByIdQueryHandler implements IQueryHandler<DiscountByIdQuery, CommandResult> {
    
    private final IDiscountRepository discountRepository;
    
    public GetDiscountByIdQueryHandler(IDiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(DiscountByIdQuery query) {
        
        var discount =
                discountRepository.findById(DiscountId.of(query.getId())).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy mã giảm giá"));
        
        
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
