package com.mts.backend.application.promotion.handler;

import com.mts.backend.application.promotion.command.CreateCouponCommand;
import com.mts.backend.domain.promotion.Coupon;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.repository.ICouponRepository;
import com.mts.backend.domain.promotion.value_object.CouponCode;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
@Service
public class CreateCouponCommandHandler implements ICommandHandler<CreateCouponCommand, CommandResult> {
    private final ICouponRepository couponRepository;
    public CreateCouponCommandHandler(ICouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }
    /**
     * @param command 
     * @return
     */
    @Override
    public CommandResult handle(CreateCouponCommand command) {
        Objects.requireNonNull(command, "Create coupon command is required");
        
        CouponCode couponCode = CouponCode.of(command.getCoupon());
        
        verifyUniqueCouponCode(couponCode);
        
        Coupon cp  = new Coupon(
                CouponId.create(),
                couponCode,
                command.getDescription(),
                LocalDateTime.now());
        
        var coupon = couponRepository.save(cp);
        
        return CommandResult.success(coupon.getId().getValue());
    }
    
    private void verifyUniqueCouponCode(CouponCode name){
        Objects.requireNonNull(name, "Coupon code is required");
        
        if (couponRepository.existByCouponCode(name)){
            throw new DuplicateException("Mã " + name + " đã tồn tại");
        }
    }

}
