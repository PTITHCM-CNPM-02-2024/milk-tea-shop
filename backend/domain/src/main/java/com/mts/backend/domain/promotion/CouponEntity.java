package com.mts.backend.domain.promotion;

import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.value_object.CouponCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "Coupon", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "coupon", columnNames = {"coupon"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponEntity extends BaseEntity<Long> {
    @Id
    @Comment("Mã coupon")
    @Column(name = "coupon_id", columnDefinition = "int UNSIGNED")
    @Getter
    @Setter
    @NotNull
    private Long id;

    @Comment("Mã giảm giá")
    @Column(name = "coupon", nullable = false, length = 15)
    @NotNull
    @Convert(converter = CouponCode.CouponCodeConverter.class)
    @Getter
    private CouponCode coupon;

    @Comment("Mô tả")
    @Column(name = "description", length = 1000)
    private String description;
    
    public boolean changeDescription(String description){
        if (Objects.equals(this.description, description)){
            return false;
        }
        
        this.description = description;
        return true;
    }
    
    public boolean changeCoupon(CouponCode coupon){
        if (Objects.equals(this.coupon, coupon)){
            return false;
        }
        
        this.coupon = coupon;
        return true;
    }
    
    public Optional<String> getDescription(){
        return Optional.ofNullable(description);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        CouponEntity that = (CouponEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}