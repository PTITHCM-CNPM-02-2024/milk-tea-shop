package com.mts.backend.application.promotion;

import com.mts.backend.application.promotion.query.DefaultDiscountQuery;
import com.mts.backend.application.promotion.query.DiscountByCouponQuery;
import com.mts.backend.application.promotion.query.DiscountByIdQuery;
import com.mts.backend.application.promotion.query_handler.GetAllDiscountCommandHandler;
import com.mts.backend.application.promotion.query_handler.GetDiscountByCouponQueryHandler;
import com.mts.backend.application.promotion.query_handler.GetDiscountByIdQueryHandler;
import com.mts.backend.shared.query.AbstractQueryBus;
import org.springframework.stereotype.Component;

@Component
public class DiscountQueryBus extends AbstractQueryBus {
    public DiscountQueryBus(GetAllDiscountCommandHandler getAllDiscountCommandHandler,
                            GetDiscountByCouponQueryHandler getDiscountByCouponQueryHandler,
                            GetDiscountByIdQueryHandler getDiscountByIdQueryHandler) {
        register(DefaultDiscountQuery.class, getAllDiscountCommandHandler);
        register(DiscountByCouponQuery.class, getDiscountByCouponQueryHandler);
        register(DiscountByIdQuery.class, getDiscountByIdQueryHandler);
    }
}
