package com.mts.backend.domain.promotion;

import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.promotion.identifier.CouponId;
import com.mts.backend.domain.promotion.value_object.CouponCode;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "coupon", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "coupon_uk", columnNames = {"coupon"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class Coupon extends BaseEntity<Long> {
    @Id
    @Comment("Mã coupon")
    @Column(name = "coupon_id", columnDefinition = "int UNSIGNED")
    @NotNull
    private Long id;

    public Coupon(@NotNull Long id, @NotNull @Pattern(regexp = "^[a-zA-Z0-9]{3,15}$",
            message = "Mã coupon phải chứa ít nhất 3 ký tự và không quá 15 ký tự") @NotBlank(message = "Mã coupon không được để trống") String coupon, String description, @Nullable Discount discount) {
        this.id = id;
        this.coupon = coupon;
        this.description = description;
        this.discount = discount;
    }

    public Coupon() {
    }

    public static CouponBuilder builder() {
        return new CouponBuilder();
    }

    public boolean setId(@NotNull CouponId couponId) {
        if (CouponId.of(this.id).equals(couponId)) {
            return false;
        }

        this.id = couponId.getValue();
        return true;
    }

    @Comment("Mã giảm giá")
    @Column(name = "coupon", nullable = false, length = 15)
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9]{3,15}$",
            message = "Mã coupon phải chứa ít nhất 3 ký tự và không quá 15 ký tự")
    @NotBlank(message = "Mã coupon không được để trống")
    private String coupon;

    @Comment("Mô tả")
    @Column(name = "description", length = 1000)
    private String description;

    @Nullable
    @OneToOne(mappedBy = "coupon")
    private Discount discount;

    public boolean setDiscount(@Nullable Discount discount) {
        if (Objects.equals(this.discount, discount)) {
            return false;
        }

        this.discount = discount;
        return true;
    }

    public Optional<Discount> getDiscount() {
        return Optional.ofNullable(discount);
    }

    public boolean changeDescription(String description) {
        if (Objects.equals(this.description, description)) {
            return false;
        }

        this.description = description;
        return true;
    }

    public boolean setCoupon(@NotNull CouponCode coupon) {
        if (CouponCode.of(this.coupon).equals(coupon)) {
            return false;
        }

        this.coupon = coupon.getValue();
        return true;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Coupon that = (Coupon) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public @NotNull Long getId() {
        return this.id;
    }

    public @NotNull @Pattern(regexp = "^[a-zA-Z0-9]{3,15}$",
            message = "Mã coupon phải chứa ít nhất 3 ký tự và không quá 15 ký tự") @NotBlank(message = "Mã coupon không được để trống") String getCoupon() {
        return this.coupon;
    }

    public static class CouponBuilder {
        private @NotNull Long id;
        private @NotNull
        @Pattern(regexp = "^[a-zA-Z0-9]{3,15}$",
                message = "Mã coupon phải chứa ít nhất 3 ký tự và không quá 15 ký tự")
        @NotBlank(message = "Mã coupon không được để trống") String coupon;
        private String description;
        private Discount discount;

        CouponBuilder() {
        }

        public CouponBuilder id(@NotNull Long id) {
            this.id = id;
            return this;
        }

        public CouponBuilder coupon(@NotNull CouponCode coupon) {
            this.coupon = coupon.getValue();
            return this;
        }

        public CouponBuilder description(String description) {
            this.description = description;
            return this;
        }

        public CouponBuilder discount(Discount discount) {
            this.discount = discount;
            return this;
        }

        public Coupon build() {
            return new Coupon(this.id, this.coupon, this.description, this.discount);
        }

        public String toString() {
            return "Coupon.CouponBuilder(id=" + this.id + ", coupon=" + this.coupon + ", description=" + this.description + ", discount=" + this.discount + ")";
        }
    }
}