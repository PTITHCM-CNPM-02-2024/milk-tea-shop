package com.mts.backend.application.promotion.handler;

import com.mts.backend.application.promotion.command.UpdateCouponCommand;
import com.mts.backend.domain.promotion.CouponEntity;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.jpa.JpaCouponRepository;
import com.mts.backend.domain.promotion.value_object.CouponCode;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateCouponCommandHandler implements ICommandHandler<UpdateCouponCommand, CommandResult> {
    private final JpaCouponRepository couponRepository;
    
    public UpdateCouponCommandHandler(JpaCouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }
    
    @Override
    @Transactional
    public CommandResult handle(UpdateCouponCommand command) {
        Objects.requireNonNull(command, "Update coupon command is required");
        
        var cp = mustExistCoupon(command.getId());
        
        if (cp.changeCoupon(command.getCoupon())) {
            verifyUniqueCouponCode(command.getId(), cp.getCoupon());
        }
        
        cp.changeDescription(command.getDescription().orElse(null));
        
        
        return CommandResult.success(cp.getId());
    }
    
    private CouponEntity mustExistCoupon(CouponId id){
        Objects.requireNonNull(id, "Coupon id is required");
        
        return couponRepository.findById(id.getValue()).orElseThrow(() -> new NotFoundException("Không tìm thấy mã giảm giá"));
    }
    
    private void verifyUniqueCouponCode( CouponId id, CouponCode name) {
        
        Objects.requireNonNull(name, "Coupon code is required");
        
        if (couponRepository.existsByIdNotAndCoupon(id.getValue(), name)) {
            throw new DuplicateException("Mã " + name.getValue() + " đã tồn tại");
        }
    }
}
