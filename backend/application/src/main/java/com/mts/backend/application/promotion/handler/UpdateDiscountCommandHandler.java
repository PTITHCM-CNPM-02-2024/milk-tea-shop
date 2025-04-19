package com.mts.backend.application.promotion.handler;

import com.mts.backend.application.promotion.command.UpdateDiscountCommand;
import com.mts.backend.domain.promotion.Discount;
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
import org.springframework.dao.DataIntegrityViolationException;
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
        
        try{
            var discount = mustExistDiscount(command.getId());
            var newVal = PromotionDiscountValue.builder()
                    .unit(command.getDiscountUnit())
                    .value(command.getDiscountValue())
                    .maxDiscountAmount(command.getMaxDiscountAmount())
                    .build();

            discount.setCoupon(couponRepository.getReferenceById(command.getCouponId().getValue()));
            discount.setName(command.getName());
            discount.changeDescription(command.getDescription().orElse(null));
            discount.setPromotionDiscountValue(newVal);
            discount.setMinRequiredOrderValue(command.getMinimumOrderValue());
            discount.setMinRequiredProduct(command.getMinimumRequiredProduct().orElse(null));
            discount.setValidFrom(command.getValidFrom().orElse(null));
            discount.setValidUntil(command.getValidUntil());
            discount.setMaxUse(command.getMaxUsage().orElse(null));
            discount.setMaxUsesPerCustomer(command.getMaxUsagePerCustomer().orElse(null));
            discount.setActive(command.getActive());

            return CommandResult.success(discount.getId());
        }catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("uk_discount_name")){
                throw new NotFoundException("Tên mã giảm giá đã tồn tại");
            }
            if (e.getMessage().contains("uk_discount_coupon_id")){
                throw new NotFoundException("Mã giảm giá đã tồn tại");
            }
            throw new RuntimeException("Đã có lỗi xảy ra khi cập nhật mã giảm giá", e);
        }
        
    }
    protected Discount mustExistDiscount(DiscountId id){
        Objects.requireNonNull(id, "Discount id is required");
        
        return discountRepository.findById(id.getValue()).orElseThrow(() -> new NotFoundException("Không tìm thấy chương " +
                "trình giảm giá"));
    }
    
}
