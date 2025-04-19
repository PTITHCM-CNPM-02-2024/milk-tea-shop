package com.mts.backend.application.promotion.query_handler;

import java.util.Objects;

import com.mts.backend.application.promotion.query.UnusedCouponQuery;
import com.mts.backend.application.promotion.response.CouponDetailResponse;
import com.mts.backend.domain.promotion.jpa.JpaCouponRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UnusedCouponQueryHandler implements IQueryHandler<UnusedCouponQuery,CommandResult> {

    private final JpaCouponRepository jpaCouponRepository;

    public UnusedCouponQueryHandler(JpaCouponRepository jpaCouponRepository) {
        this.jpaCouponRepository = jpaCouponRepository;
    }

    @Override
    public CommandResult handle(UnusedCouponQuery query) {
        Objects.requireNonNull(query, "UnusedCouponQuery is required");

        var coupons = jpaCouponRepository.findAllUnusedCoupons(Pageable.ofSize(query.getSize()).withPage(query.getPage())) ;
        
        var list = coupons.map(coupon ->{
                return CouponDetailResponse.builder()
                        .id(coupon.getId())
                        .coupon(coupon.getCoupon().getValue())
                        .description(coupon.getDescription().orElse(null))
                        .build();});
        
        return CommandResult.success(list);
        
    }
}
