package com.mts.backend.application.promotion;

import com.mts.backend.application.promotion.query.CouponByIdQuery;
import com.mts.backend.application.promotion.query.DefaultCouponQuery;
import com.mts.backend.application.promotion.query_handler.GetAllCouponQueryHandler;
import com.mts.backend.application.promotion.query_handler.GetCouponByIdQueryHandler;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class CouponQueryBus extends AbstractQueryBus {

    public CouponQueryBus(GetAllCouponQueryHandler getAllCouponQueryHandler,
                          GetCouponByIdQueryHandler getCouponByIdQueryHandler) {
        register(DefaultCouponQuery.class, getAllCouponQueryHandler);
        register(CouponByIdQuery.class, getCouponByIdQueryHandler);
    }
}
