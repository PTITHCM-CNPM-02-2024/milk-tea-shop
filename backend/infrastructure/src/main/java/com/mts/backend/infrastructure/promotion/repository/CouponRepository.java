package com.mts.backend.infrastructure.promotion.repository;

import com.mts.backend.domain.promotion.Coupon;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.repository.ICouponRepository;
import com.mts.backend.domain.promotion.value_object.CouponCode;
import com.mts.backend.infrastructure.persistence.entity.CouponEntity;
import com.mts.backend.infrastructure.promotion.jpa.JpaCouponRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CouponRepository implements ICouponRepository {
    private final JpaCouponRepository jpaCouponRepository;

    public CouponRepository(JpaCouponRepository jpaCouponRepository) {
        this.jpaCouponRepository = jpaCouponRepository;
    }

    /**
     * @param coupon
     * @return
     */
    @Override
    @Transactional
    public Coupon save(Coupon coupon) {
        Objects.requireNonNull(coupon, "Coupon must not be null");
        
        try {
            if (jpaCouponRepository.existsById(coupon.getId().getValue())) {
                return update(coupon);
            } else {
                return create(coupon);
            }
        }catch (Exception e){
            throw  e;
        }
    }

    @Transactional
    protected Coupon create(Coupon coupon) {
        Objects.requireNonNull(coupon, "Coupon must not be null");

        CouponEntity en = CouponEntity.builder()
                .id(coupon.getId().getValue())
                .coupon(coupon.getCoupon().getValue())
                .description(coupon.getDescription())
                .build();
        jpaCouponRepository.insertCoupon(en);
        
        return coupon;
    }
    
    @Transactional
    protected Coupon update(Coupon coupon) {
        Objects.requireNonNull(coupon, "Coupon must not be null");

        CouponEntity en = CouponEntity.builder()
                .id(coupon.getId().getValue())
                .coupon(coupon.getCoupon().getValue())
                .description(coupon.getDescription())
                .build();
        jpaCouponRepository.updateCoupon(en);
        
        return coupon;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Optional<Coupon> findById(CouponId id) {
        Objects.requireNonNull(id, "Coupon id must not be null");
        
        return jpaCouponRepository.findById(id.getValue())
                .map(e -> new Coupon(
                        CouponId.of(e.getId()),
                        CouponCode.of(e.getCoupon()),
                        e.getDescription(),
                        e.getUpdatedAt().orElse(LocalDateTime.now())));
    }

    /**
     * @return
     */
    @Override
    public List<Coupon> findAll() {
        return jpaCouponRepository.findAll()
                .stream()
                .map(e -> new Coupon(
                        CouponId.of(e.getId()),
                        CouponCode.of(e.getCoupon()),
                        e.getDescription(),
                        e.getUpdatedAt().orElse(LocalDateTime.now())))
                .toList();
    }

    /**
     * @param couponCode
     * @return
     */
    @Override
    public Optional<Coupon> findByCouponCode(CouponCode couponCode) {
        Objects.requireNonNull(couponCode, "Coupon code must not be null");
        
        return jpaCouponRepository.findByCoupon(couponCode.getValue())
                .map(e -> new Coupon(
                        CouponId.of(e.getId()),
                        CouponCode.of(e.getCoupon()),
                        e.getDescription(),
                        e.getUpdatedAt().orElse(LocalDateTime.now())));
    }

    /**
     * @param name 
     * @return
     */
    @Override
    public boolean existByCouponCode(CouponCode name) {
        Objects.requireNonNull(name, "CouponCode is required");
        
        return jpaCouponRepository.existsByCoupon(name.getValue());
    }

    /**
     * @param id 
     * @return
     */
    @Override
    public boolean existById(CouponId id) {
        Objects.requireNonNull(id, "Coupon id is required");
        
        return jpaCouponRepository.existsById(id.getValue());
    }
}
