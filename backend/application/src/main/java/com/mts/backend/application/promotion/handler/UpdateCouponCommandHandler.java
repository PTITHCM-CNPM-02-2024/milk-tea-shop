package com.mts.backend.application.promotion.handler;

import com.mts.backend.application.promotion.command.UpdateCouponCommand;
import com.mts.backend.domain.promotion.Coupon;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.jpa.JpaCouponRepository;
import com.mts.backend.domain.promotion.value_object.CouponCode;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
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
        
        try{
            var cp = mustExistCoupon(command.getId());

            cp.setCoupon(command.getCoupon());

            cp.changeDescription(command.getDescription().orElse(null));


            return CommandResult.success(cp.getId());
        }catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("uk_coupon_coupon")){
                throw new DuplicateException("Mã coupon đã tồn tại");
            }
            throw new RuntimeException("Đã có lỗi xảy ra khi cập nhật coupon", e);
        }
    }
    
    private Coupon mustExistCoupon(CouponId id){
        Objects.requireNonNull(id, "Coupon id is required");
        
        return couponRepository.findById(id.getValue()).orElseThrow(() -> new NotFoundException("Không tìm thấy mã giảm giá"));
    }
    
}
