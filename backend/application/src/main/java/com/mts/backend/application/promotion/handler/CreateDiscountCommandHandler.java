package com.mts.backend.application.promotion.handler;

import com.mts.backend.application.promotion.command.CreateDiscountCommand;
import com.mts.backend.domain.promotion.Discount;
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
import org.springframework.dao.DataIntegrityViolationException;
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

        Objects.requireNonNull(command, "Create discount command is required");

        try{
            PromotionDiscountValue value = PromotionDiscountValue
                    .builder()
                    .unit(command.getDiscountUnit())
                    .value(command.getDiscountValue())
                    .maxDiscountAmount(command.getMaxDiscountAmount())
                    .build();

            Discount discount = Discount.builder()
                    .id(DiscountId.create().getValue())
                    .name(command.getName())
                    .description(command.getDescription().orElse(null))
                    .coupon(couponRepository.getReferenceById(command.getCouponId().getValue()))
                    .promotionDiscountValue(value)
                    .minRequiredOrderValue(command.getMinimumOrderValue())
                    .minRequiredProduct(command.getMinimumRequiredProduct().orElse(null))
                    .validFrom(command.getValidFrom().orElse(null))
                    .validUntil(command.getValidUntil())
                    .maxUsesPerCustomer(command.getMaxUsagePerCustomer().orElse(null))
                    .maxUse(command.getMaxUsage().orElse(null))
                    .currentUse(0L)
                    .active(true)
                    .build();


            discount = discountRepository.save(discount);

            return CommandResult.success(discount.getId());
        }catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("uk_discount_name")) {
                throw new DuplicateException("Tên " + command.getName() + " đã tồn tại");
            }
            if (e.getMessage().contains("uk_discount_coupon_id")) {
                throw new DuplicateException("Coupon " + command.getCouponId() + " đã tồn tại");
            }
            if(e.getMessage().contains("fk_discount_coupon")){
                throw new NotFoundException("Coupon " + command.getCouponId() + " không tồn tại");
            }
            throw new DuplicateException("Đã có lỗi xảy ra khi tạo giảm giá", e);
        }
    }
    
}
