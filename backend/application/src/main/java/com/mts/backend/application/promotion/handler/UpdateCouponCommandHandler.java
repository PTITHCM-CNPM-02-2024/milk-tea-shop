package com.mts.backend.application.promotion.handler;

import com.mts.backend.application.promotion.command.UpdateCouponCommand;
import com.mts.backend.domain.promotion.Coupon;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.repository.ICouponRepository;
import com.mts.backend.domain.promotion.value_object.CouponCode;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateCouponCommandHandler implements ICommandHandler<UpdateCouponCommand, CommandResult> {
    private final ICouponRepository couponRepository;
    
    public UpdateCouponCommandHandler(ICouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }
    
    @Override
    public CommandResult handle(UpdateCouponCommand command) {
        Objects.requireNonNull(command, "Update coupon command is required");
        
        Coupon cp = mustExistCoupon(CouponId.of(command.getId()));
        
        CouponCode couponCode = CouponCode.of(command.getCoupon());
        
        if (cp.changeCoupon(couponCode)) {
            verifyUniqueCouponCode(couponCode);
        }
        
        cp.changeDescription(command.getDescription());
        
        var coupon = couponRepository.save(cp);
        
        return CommandResult.success(coupon.getId().getValue());
    }
    
    private Coupon mustExistCoupon(CouponId id){
        Objects.requireNonNull(id, "Coupon id is required");
        
        return couponRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy mã giảm giá"));
    }
    
    private void verifyUniqueCouponCode(CouponCode name) {
        
        Objects.requireNonNull(name, "Coupon code is required");
        
        if (couponRepository.existByCouponCode(name)) {
            throw new DuplicateException("Mã " + name + " đã tồn tại");
        }
    }
}
