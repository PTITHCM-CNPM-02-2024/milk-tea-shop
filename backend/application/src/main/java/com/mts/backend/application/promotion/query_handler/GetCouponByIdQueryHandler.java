package com.mts.backend.application.promotion.query_handler;

import com.mts.backend.application.promotion.query.CouponByIdQuery;
import com.mts.backend.application.promotion.response.CouponDetailResponse;
import com.mts.backend.domain.promotion.Coupon;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.jpa.JpaCouponRepository;
import com.mts.backend.domain.promotion.repository.ICouponRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetCouponByIdQueryHandler implements IQueryHandler<CouponByIdQuery, CommandResult> {
    private final JpaCouponRepository couponRepository;
    
    public GetCouponByIdQueryHandler(JpaCouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(CouponByIdQuery query) {
        Objects.requireNonNull(query, "Coupon by id query is required");
        
        var coupon = couponRepository.findById(query.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá"));

        CouponDetailResponse response = CouponDetailResponse.builder()
                .id(coupon.getId().getValue())
                .coupon(coupon.getCoupon().getValue())
                .description(coupon.getDescription().orElse(null))
                .build();
        
        return CommandResult.success(response);
    }
}
