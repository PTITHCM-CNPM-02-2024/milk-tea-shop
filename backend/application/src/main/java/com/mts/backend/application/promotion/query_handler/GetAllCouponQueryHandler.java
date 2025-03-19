package com.mts.backend.application.promotion.query_handler;

import com.mts.backend.application.promotion.query.DefaultCouponQuery;
import com.mts.backend.application.promotion.response.CouponDetailResponse;
import com.mts.backend.domain.promotion.repository.ICouponRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllCouponQueryHandler implements IQueryHandler<DefaultCouponQuery, CommandResult> {
    private final ICouponRepository couponRepository;
    
    public GetAllCouponQueryHandler(ICouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(DefaultCouponQuery query) {
        Objects.requireNonNull(query, "Default coupon query is required");
        
        var coupons = couponRepository.findAll();
        
        List<CouponDetailResponse> responses = new ArrayList<>();
        
        coupons.forEach(coupon -> {
            responses.add(CouponDetailResponse.builder()
                    .id(coupon.getId().getValue())
                    .coupon(coupon.getCoupon().getValue())
                    .description(coupon.getDescription())
                    .build());
        });
        
        return CommandResult.success(responses);
    }
}
