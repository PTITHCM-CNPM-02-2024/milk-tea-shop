package com.mts.backend.application.promotion.handler;

import com.mts.backend.application.promotion.command.CreateDiscountCommand;
import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.common.value_object.DiscountUnit;
import com.mts.backend.domain.common.value_object.MemberDiscountValue;
import com.mts.backend.domain.promotion.Discount;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.identifier.DiscountId;
import com.mts.backend.domain.promotion.repository.ICouponRepository;
import com.mts.backend.domain.promotion.repository.IDiscountRepository;
import com.mts.backend.domain.promotion.value_object.DiscountName;
import com.mts.backend.domain.promotion.value_object.PromotionDiscountValue;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CreateDiscountCommandHandler implements ICommandHandler<CreateDiscountCommand, CommandResult> {
    private final IDiscountRepository discountRepository;
    private final ICouponRepository couponRepository;
    
    public CreateDiscountCommandHandler(IDiscountRepository discountRepository, ICouponRepository couponRepository) {
        this.discountRepository = discountRepository;
        this.couponRepository = couponRepository;
    }
    /**
     * @param command 
     * @return
     */
    @Override
    public CommandResult handle(CreateDiscountCommand command) {

        DiscountName name = DiscountName.of(command.getName());
        verifyUniqueName(name);

        CouponId couponId = CouponId.of(command.getCouponId());
        mustExitsCoupon(couponId);

        PromotionDiscountValue value = PromotionDiscountValue.of(command.getDiscountValue(),
                DiscountUnit.valueOf(command.getDiscountUnit()), Money.of(command.getMaxDiscountAmount()));
        Discount discount = new Discount(
                DiscountId.create(),
                name,
                command.getDescription(),
                couponId,
                value,
                Money.of(command.getMinimumOrderValue()),
                command.getMinimumRequiredProduct(),
                command.getValidFrom(),
                command.getValidUntil(),
                command.getMaxUsage(),
                command.getMaxUsagePerCustomer(),
                null,
                true,
                LocalDateTime.now());
        
        discountRepository.save(discount);
        
        return CommandResult.success(discount.getId().getValue());
    }
    
    private void mustExitsCoupon(CouponId id){
        
        Objects.requireNonNull(id, "Coupon id is required");
        
        if (!couponRepository.existById(id)){
            throw new NotFoundException("Mã giảm giá không tồn tại");
        }
        
    }
    
    private void verifyUniqueName(DiscountName name){
        Objects.requireNonNull(name, "Discount name is required");
        
        if (discountRepository.existByName(name)){
            throw new DuplicateException("Tên " + name + " đã tồn tại");
        }
    }
}
