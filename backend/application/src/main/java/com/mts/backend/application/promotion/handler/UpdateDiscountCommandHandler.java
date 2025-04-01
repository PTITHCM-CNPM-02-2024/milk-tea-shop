package com.mts.backend.application.promotion.handler;

import com.mts.backend.application.promotion.command.UpdateDiscountCommand;
import com.mts.backend.domain.promotion.DiscountEntity;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.identifier.DiscountId;
import com.mts.backend.domain.promotion.jpa.JpaCouponRepository;
import com.mts.backend.domain.promotion.jpa.JpaDiscountRepository;
import com.mts.backend.domain.promotion.value_object.DiscountName;
import com.mts.backend.domain.promotion.value_object.PromotionDiscountValue;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateDiscountCommandHandler implements ICommandHandler<UpdateDiscountCommand, CommandResult> {
    private final JpaDiscountRepository discountRepository;
    private final JpaCouponRepository couponRepository;
    public UpdateDiscountCommandHandler(JpaDiscountRepository discountRepository, JpaCouponRepository couponRepository) {
        this.discountRepository = discountRepository;
        this.couponRepository = couponRepository;
    }
    @Override
    @Transactional
    public CommandResult handle(UpdateDiscountCommand command) {
        Objects.requireNonNull(command, "Update discount command is required");
        
        var discount = mustExistDiscount(command.getId());
        CouponId checkCouponId = command.getCouponId();
        var newVal = PromotionDiscountValue.builder()
                .unit(command.getDiscountUnit())
                .value(command.getDiscountValue())
                .maxDiscountAmount(command.getMaxDiscountAmount())
                .build();
        
        if (discount.getCouponEntity().getId().equals(checkCouponId.getValue())) {
            verifyUniqueCoupon(command.getId(), checkCouponId);
            discount.setCouponEntity(couponRepository.getReferenceById(checkCouponId.getValue()));
        }
        
        if (discount.changeDiscountName(command.getName())) {
            verifyUniqueName(command.getId(), command.getName());
        }
        discount.changeDescription(command.getDescription().orElse(null));
        discount.changePromotionDiscountValue(newVal);
        discount.changeMinRequiredOrderValue(command.getMinimumOrderValue());
        discount.changeMinRequiredProduct(command.getMinimumRequiredProduct().orElse(null));
        discount.changeValidFrom(command.getValidFrom().orElse(null));
        discount.changeValidUntil(command.getValidUntil());
        discount.changeMaxUse(command.getMaxUsage().orElse(null));
        discount.changeMaxUsesPerCustomer(command.getMaxUsagePerCustomer().orElse(null));
        discount.setActive(command.getActive());
        
        
        
        return CommandResult.success(discount.getId());
        
    }
    protected DiscountEntity mustExistDiscount(DiscountId id){
        Objects.requireNonNull(id, "Discount id is required");
        
        return discountRepository.findById(id.getValue()).orElseThrow(() -> new NotFoundException("Không tìm thấy chương " +
                "trình giảm giá"));
    }
    
    protected void verifyUniqueCoupon(DiscountId id, CouponId couponId) {
        Objects.requireNonNull(id, "Coupon id is required");
        
        if (!couponRepository.existsById(id.getValue())){
            throw new NotFoundException("Không tìm thấy mã giảm giá");
        }
        
        if (discountRepository.existsByIdNotAndCouponEntity_Id(id.getValue(), couponId.getValue())) {
            throw new NotFoundException("Mã giảm giá đã tồn tại");
        }
    }
    
    private void verifyUniqueName(DiscountId id, DiscountName name) {
        Objects.requireNonNull(id, "Discount id is required");
        
        if (discountRepository.existsByIdNotAndName(id.getValue(), name)) {
            throw new NotFoundException("Tên mã giảm giá đã tồn tại");
        }
    }
    
}
