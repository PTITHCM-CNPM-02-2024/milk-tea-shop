package com.mts.backend.infrastructure.promotion.repository;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.common.value_object.MemberDiscountValue;
import com.mts.backend.domain.promotion.Discount;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.identifier.DiscountId;
import com.mts.backend.domain.promotion.repository.IDiscountRepository;
import com.mts.backend.domain.promotion.value_object.DiscountName;
import com.mts.backend.domain.promotion.value_object.PromotionDiscountValue;
import com.mts.backend.infrastructure.persistence.entity.CouponEntity;
import com.mts.backend.infrastructure.persistence.entity.DiscountEntity;
import com.mts.backend.infrastructure.promotion.jpa.JpaDiscountRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DiscountRepository implements IDiscountRepository {
    private final JpaDiscountRepository jpaDiscountRepository;
    
    public DiscountRepository(JpaDiscountRepository jpaDiscountRepository) {
        this.jpaDiscountRepository = jpaDiscountRepository;
    }
    /**
     * @param id 
     * @return
     */
    @Override
    public Optional<Discount> findById(DiscountId id) {
        Objects.requireNonNull(id, "Discount id must not be null");
        
        return jpaDiscountRepository.findById(id.getValue()).map(
                e -> new Discount(
                        DiscountId.of(e.getId()),
                        DiscountName.of(e.getName()),
                        e.getDescription(),
                        CouponId.of(e.getCouponEntity().getId()),
                        PromotionDiscountValue.of(e.getDiscountValue(), e.getDiscountUnit(), Money.of(e.getMaxDiscountAmount())),
                        Money.of(e.getMinRequiredOrderValue()),
                        e.getMinRequiredProduct(),
                        e.getValidFrom(),
                        e.getValidUntil(),
                        e.getMaxUses(),
                        e.getMaxUsesPerCustomer(),
                        e.getCurrentUses(),
                        e.getIsActive(),
                        e.getUpdatedAt().orElse(LocalDateTime.now())));
    }

    /**
     * @param discount 
     * @return
     */
    @Override
    @Transactional
    public Discount save(Discount discount) {
        Objects.requireNonNull(discount, "Discount must not be null");
        
        try {
            if (existById(discount.getId())) {
                return update(discount);
            }
            return create(discount);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Transactional
    protected Discount create(Discount discount){

        Objects.requireNonNull(discount, "Discount must not be null");

        DiscountEntity en = DiscountEntity.builder()
                .id(discount.getId().getValue())
                .name(discount.getName().getValue())
                .description(discount.getDescription())
                .isActive(discount.isActive())
                .discountUnit(discount.getDiscountValue().getUnit())
                .discountValue(discount.getDiscountValue().getValue())
                .currentUses(discount.getCurrentUsage().orElse(null))
                .maxUses(discount.getMaxUsage().orElse(null))
                .maxUsesPerCustomer(discount.getMaxUsagePerCustomer().orElse(null))
                .validFrom(discount.getValidFrom().orElse(null))
                .validUntil(discount.getValidUntil())
                .maxDiscountAmount(discount.getDiscountValue().getMaxDiscountValue().getAmount())
                .minRequiredOrderValue(discount.getMinRequiredOrderValue().getAmount())
                .minRequiredProduct(discount.getMinRequiredProduct().orElse(null))
                .build();

        CouponEntity cp = CouponEntity.builder()
                .id(discount.getCouponId().getValue())
                .build();
        
        en.setCouponEntity(cp);
        
        jpaDiscountRepository.insertDiscount(en);
        
        return discount;
    }
    
    @Transactional
    protected Discount update(Discount discount){
        Objects.requireNonNull(discount, "Discount must not be null");

        DiscountEntity en = DiscountEntity.builder()
                .id(discount.getId().getValue())
                .name(discount.getName().getValue())
                .description(discount.getDescription())
                .isActive(discount.isActive())
                .discountUnit(discount.getDiscountValue().getUnit())
                .discountValue(discount.getDiscountValue().getValue())
                .currentUses(discount.getCurrentUsage().orElse(null))
                .maxUses(discount.getMaxUsage().orElse(null))
                .maxUsesPerCustomer(discount.getMaxUsagePerCustomer().orElse(null))
                .validFrom(discount.getValidFrom().orElse(null))
                .validUntil(discount.getValidUntil())
                .maxDiscountAmount(discount.getDiscountValue().getMaxDiscountValue().getAmount())
                .minRequiredOrderValue(discount.getMinRequiredOrderValue().getAmount())
                .minRequiredProduct(discount.getMinRequiredProduct().orElse(null))
                .build();

        CouponEntity cp = CouponEntity.builder()
                .id(discount.getCouponId().getValue())
                .build();
        
        en.setCouponEntity(cp);
        
        jpaDiscountRepository.updateDiscount(en);
        
        return discount;
    }

    /**
     * @param name 
     * @return
     */
    @Override
    public Optional<Discount> findByName(DiscountName name) {
        Objects.requireNonNull(name, "Discount name must not be null");
        
        return jpaDiscountRepository.findByName(name.getValue())
                .map(e -> new Discount(
                        DiscountId.of(e.getId()),
                        DiscountName.of(e.getName()),
                        e.getDescription(),
                        CouponId.of(e.getCouponEntity().getId()),
                        PromotionDiscountValue.of(e.getDiscountValue(), e.getDiscountUnit(), Money.of(e.getMaxDiscountAmount())),
                        Money.of(e.getMinRequiredOrderValue()),
                        e.getMinRequiredProduct(),
                        e.getValidFrom(),
                        e.getValidUntil(),
                        e.getMaxUses(),
                        e.getMaxUsesPerCustomer(),
                        e.getCurrentUses(),
                        e.getIsActive(),
                        e.getUpdatedAt().orElse(LocalDateTime.now())));
    }

    /**
     * @return 
     */
    @Override
    public List<Discount> findAll() {
        return jpaDiscountRepository.findAll()
                .stream()
                .map(e -> new Discount(
                        DiscountId.of(e.getId()),
                        DiscountName.of(e.getName()),
                        e.getDescription(),
                        CouponId.of(e.getCouponEntity().getId()),
                        PromotionDiscountValue.of(e.getDiscountValue(), e.getDiscountUnit(), Money.of(e.getMaxDiscountAmount())),
                        Money.of(e.getMinRequiredOrderValue()),
                        e.getMinRequiredProduct(),
                        e.getValidFrom(),
                        e.getValidUntil(),
                        e.getMaxUses(),
                        e.getMaxUsesPerCustomer(),
                        e.getCurrentUses(),
                        e.getIsActive(),
                        e.getUpdatedAt().orElse(LocalDateTime.now())))
                .toList();
    }

    /**
     * @return 
     */
    @Override
    public List<Discount> findAllActive() {
        return findAll().stream().filter(Discount::isActive).toList();
    }

    /**
     * @param name 
     * @return
     */
    @Override
    public boolean existByName(DiscountName name) {
        Objects.requireNonNull(name, "Discount name must not be null");
        
        return jpaDiscountRepository.existsByName(name.getValue());
    }

    /**
     * @param id 
     * @return
     */
    @Override
    public boolean existById(DiscountId id) {
        Objects.requireNonNull(id, "Discount id must not be null");
        
        return jpaDiscountRepository.existsById(id.getValue());
    }

    /**
     * @param couponId 
     * @return
     */
    @Override
    public boolean existByCouponId(CouponId couponId) {
        Objects.requireNonNull(couponId, "Coupon id must not be null");
        
        return jpaDiscountRepository.existsByCouponEntity_Id(couponId.getValue());
    }

    /**
     * @param couponId 
     * @return
     */
    @Override
    public Optional<Discount> findByCouponId(CouponId couponId) {
        Objects.requireNonNull(couponId, "Coupon id must not be null");
        
        return jpaDiscountRepository.findByCouponEntity_Id(couponId.getValue())
                .map(e -> new Discount(
                        DiscountId.of(e.getId()),
                        DiscountName.of(e.getName()),
                        e.getDescription(),
                        CouponId.of(e.getCouponEntity().getId()),
                        PromotionDiscountValue.of(e.getDiscountValue(), e.getDiscountUnit(), Money.of(e.getMaxDiscountAmount())),
                        Money.of(e.getMinRequiredOrderValue()),
                        e.getMinRequiredProduct(),
                        e.getValidFrom(),
                        e.getValidUntil(),
                        e.getMaxUses(),
                        e.getMaxUsesPerCustomer(),
                        e.getCurrentUses(),
                        e.getIsActive(),
                        e.getUpdatedAt().orElse(LocalDateTime.now())));
    }
}
