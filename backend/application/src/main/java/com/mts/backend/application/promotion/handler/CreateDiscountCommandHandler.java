package com.mts.backend.application.promotion.handler;

import com.mts.backend.application.promotion.command.CreateDiscountCommand;
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
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CreateDiscountCommandHandler implements ICommandHandler<CreateDiscountCommand, CommandResult> {
    private final JpaDiscountRepository discountRepository;
    private final JpaCouponRepository couponRepository;
    
    public CreateDiscountCommandHandler(JpaDiscountRepository discountRepository, JpaCouponRepository couponRepository) {
        this.discountRepository = discountRepository;
        this.couponRepository = couponRepository;
    }
    /**
     * @param command 
     * @return
     */
    @Override
    @Transactional
    public CommandResult handle(CreateDiscountCommand command) {
        
        verifyUniqueName(command.getName());
        verifyUniqueCoupon(command.getCouponId());
        
        PromotionDiscountValue value = PromotionDiscountValue
                .builder()
                .unit(command.getDiscountUnit())
                .value(command.getDiscountValue())
                .maxDiscountAmount(command.getMaxDiscountAmount())
                .build();

        DiscountEntity discount = DiscountEntity.builder()
                .id(DiscountId.create())
                .name(command.getName())
                .description(command.getDescription().orElse(null))
                .couponEntity(couponRepository.getReferenceById(command.getCouponId()))
                .promotionDiscountValue(value)
                .minRequiredOrderValue(command.getMinimumOrderValue())
                .minRequiredProduct(command.getMinimumRequiredProduct().orElse(null))
                .validFrom(command.getValidFrom().orElse(null))
                .validUntil(command.getValidUntil())
                .maxUsesPerCustomer(command.getMaxUsagePerCustomer().orElse(null))
                .maxUse(command.getMaxUsage().orElse(null))
                .build();
        
        try{
            discount = discountRepository.save(discount);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Không tìm thấy mã giảm giá");
        }
        
        return CommandResult.success(discount.getId().getValue());
    }
    
    private void verifyUniqueCoupon(CouponId couponId){
        Objects.requireNonNull(couponId, "Coupon id is required");
        
        if (!discountRepository.existsByCouponEntity_Id(couponId)){
            throw new DuplicateException("Coupon " + couponId + " đã tồn tại");
        }
    }
    private void verifyUniqueName(DiscountName name){
        Objects.requireNonNull(name, "Discount name is required");
        
        if (discountRepository.existsByName(name)){
            throw new DuplicateException("Tên " + name + " đã tồn tại");
        }
    }
}
