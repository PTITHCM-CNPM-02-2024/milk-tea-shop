package com.mts.backend.application.promotion.handler;

import com.mts.backend.application.promotion.command.UpdateDiscountCommand;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.common.value_object.DiscountUnit;
import com.mts.backend.domain.promotion.Discount;
import com.mts.backend.domain.promotion.DiscountEntity;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.identifier.DiscountId;
import com.mts.backend.domain.promotion.jpa.JpaCouponRepository;
import com.mts.backend.domain.promotion.jpa.JpaDiscountRepository;
import com.mts.backend.domain.promotion.value_object.DiscountName;
import com.mts.backend.domain.promotion.value_object.PromotionDiscountValue;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
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
        
        if (discount.getCouponEntity().getId().equals(checkCouponId)) {
            verifyUniqueCoupon(checkCouponId);
            discount.setCouponEntity(couponRepository.getReferenceById(checkCouponId));
        }
        
        discount.changeDiscountName(command.getName());
        discount.changeDescription(command.getDescription().orElse(null));
        discount.changePromotionDiscountValue(newVal);
        discount.changeMinRequiredOrderValue(command.getMinimumOrderValue());
        discount.changeMinRequiredProduct(command.getMinimumRequiredProduct().orElse(null));
        discount.changeValidFrom(command.getValidFrom().orElse(null));
        discount.changeValidUntil(command.getValidUntil());
        discount.changeMaxUse(command.getMaxUsage().orElse(null));
        discount.changeMaxUsesPerCustomer(command.getMaxUsagePerCustomer().orElse(null));
        discount.setActive(command.getActive());
        
        
        var updatedDiscount = discountRepository.save(discount);
        
        return CommandResult.success(updatedDiscount.getId().getValue());
        
    }
    @Transactional
    protected DiscountEntity mustExistDiscount(DiscountId id){
        Objects.requireNonNull(id, "Discount id is required");
        
        return discountRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy chương trình giảm giá"));
    }
    
    @Transactional
    protected void verifyUniqueCoupon(CouponId id){
        Objects.requireNonNull(id, "Coupon id is required");
        
        if (!couponRepository.existsById(id)){
            throw new NotFoundException("Không tìm thấy mã giảm giá");
        }
    }
}
