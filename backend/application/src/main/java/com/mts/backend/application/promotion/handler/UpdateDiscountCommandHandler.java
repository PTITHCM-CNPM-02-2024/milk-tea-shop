package com.mts.backend.application.promotion.handler;

import com.mts.backend.application.promotion.command.UpdateDiscountCommand;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.customer.value_object.DiscountUnit;
import com.mts.backend.domain.customer.value_object.DiscountValue;
import com.mts.backend.domain.promotion.Coupon;
import com.mts.backend.domain.promotion.Discount;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.identifier.DiscountId;
import com.mts.backend.domain.promotion.repository.ICouponRepository;
import com.mts.backend.domain.promotion.repository.IDiscountRepository;
import com.mts.backend.domain.promotion.value_object.DiscountName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateDiscountCommandHandler implements ICommandHandler<UpdateDiscountCommand, CommandResult> {
    private final IDiscountRepository discountRepository;
    private final ICouponRepository couponRepository;
    public UpdateDiscountCommandHandler(IDiscountRepository discountRepository, ICouponRepository couponRepository) {
        this.discountRepository = discountRepository;
        this.couponRepository = couponRepository;
    }
    @Override
    public CommandResult handle(UpdateDiscountCommand command) {
        Objects.requireNonNull(command, "Update discount command is required");
        
        Discount discount = mustExistDiscount(DiscountId.of(command.getId()));
        CouponId checkCouponId = CouponId.of(command.getCouponId());
        var newVal = DiscountValue.of(command.getDiscountValue(), DiscountUnit.valueOf(command.getDiscountUnit()));
        
        if (discount.changeCouponId(checkCouponId)){
            verifyUniqueCoupon(checkCouponId);
        }
        
        discount.changeName(DiscountName.of(command.getName()));
        discount.changeDescription(command.getDescription());
        discount.changeValue(newVal);
        discount.changeMaxDiscountAmount(Money.of(command.getMaxDiscountAmount()));
        discount.changeMinRequiredOrderValue(Money.of(command.getMinimumOrderValue()));
        discount.changeMinRequiredProduct(command.getMinimumRequiredProduct());
        discount.changeValidFrom(command.getValidFrom());
        discount.changeValidUntil(command.getValidUntil());
        discount.changeMaxUsage(command.getMaxUsage());
        discount.changeMaxUsagePerCustomer(command.getMaxUsagePerCustomer());
        discount.changeActive(command.getIsActive());
        
        
        var updatedDiscount = discountRepository.save(discount);
        
        return CommandResult.success(updatedDiscount.getId().getValue());
        
    }
    
    private Discount mustExistDiscount(DiscountId id){
        Objects.requireNonNull(id, "Discount id is required");
        
        return discountRepository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy chương trình giảm giá"));
    }
    
    private void verifyUniqueCoupon(CouponId id){
        Objects.requireNonNull(id, "Coupon id is required");
        
        if (!couponRepository.existById(id)){
            throw new NotFoundException("Không tìm thấy mã giảm giá");
        }
        
        if (discountRepository.existByCouponId(id)){
            throw new DuplicateException("Mã giảm giá đã được sử dụng");
        }
    }
}
