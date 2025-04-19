package com.mts.backend.application.promotion.handler;

import com.mts.backend.application.promotion.command.CreateCouponCommand;
import com.mts.backend.domain.promotion.Coupon;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.jpa.JpaCouponRepository;
import com.mts.backend.domain.promotion.value_object.CouponCode;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Objects;
@Service
public class CreateCouponCommandHandler implements ICommandHandler<CreateCouponCommand, CommandResult> {
    private final JpaCouponRepository couponRepository;
    public CreateCouponCommandHandler(JpaCouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }
    /**
     * @param command 
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(CreateCouponCommand command) {
        Objects.requireNonNull(command, "Create coupon command is required");
        
        try {
            Coupon en  = Coupon.builder()
                    .id(CouponId.create().getValue())
                    .coupon(command.getCoupon())
                    .description(command.getDescription().orElse(null))
                    .build();

            var coupon = couponRepository.save(en);

            return CommandResult.success(coupon.getId());
        }catch(DataIntegrityViolationException e){
            if (e.getMessage().contains("uk_coupon_coupon")){
                throw new DuplicateException("Mã coupon đã tồn tại");
            }
            throw new RuntimeException("Đã có lỗi xảy ra khi tạo coupon", e);
        }
    }
    
}
