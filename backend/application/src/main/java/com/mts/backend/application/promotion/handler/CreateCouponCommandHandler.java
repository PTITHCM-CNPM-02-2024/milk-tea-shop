package com.mts.backend.application.promotion.handler;

import com.mts.backend.application.promotion.command.CreateCouponCommand;
import com.mts.backend.domain.promotion.Coupon;
import com.mts.backend.domain.promotion.CouponEntity;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.jpa.JpaCouponRepository;
import com.mts.backend.domain.promotion.repository.ICouponRepository;
import com.mts.backend.domain.promotion.value_object.CouponCode;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        
        
        verifyUniqueCouponCode(command.getCoupon());

        CouponEntity en  = CouponEntity.builder()
                .id(CouponId.create())
                .coupon(command.getCoupon())
                .description(command.getDescription().orElse(null))
                .build();
        
        var coupon = couponRepository.save(en);
        
        return CommandResult.success(coupon.getId().getValue());
    }
    
    private void verifyUniqueCouponCode(CouponCode name){
        Objects.requireNonNull(name, "Coupon code is required");
        
        if (couponRepository.existsByCoupon(name)){
            throw new DuplicateException("Mã " + name + " đã tồn tại");
        }
    }

}
