package com.mts.backend.application.promotion.query_handler;

import com.mts.backend.application.promotion.query.DefaultCouponQuery;
import com.mts.backend.application.promotion.response.CouponDetailResponse;
import com.mts.backend.domain.promotion.jpa.JpaCouponRepository;
import com.mts.backend.domain.promotion.repository.ICouponRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllCouponQueryHandler implements IQueryHandler<DefaultCouponQuery, CommandResult> {
    private final JpaCouponRepository couponRepository;
    
    public GetAllCouponQueryHandler(JpaCouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(DefaultCouponQuery query) {
        Objects.requireNonNull(query, "Default coupon query is required");
        
        var coupons = couponRepository.findAll(Pageable.ofSize(query.getSize()).withPage(query.getPage()));
        
        List<CouponDetailResponse> responses = new ArrayList<>();
        
        coupons.forEach(coupon -> {
            responses.add(CouponDetailResponse.builder()
                    .id(coupon.getId().getValue())
                    .coupon(coupon.getCoupon().getValue())
                    .description(coupon.getDescription().orElse(null))
                    .build());
        });
        
        return CommandResult.success(responses);
    }
}
