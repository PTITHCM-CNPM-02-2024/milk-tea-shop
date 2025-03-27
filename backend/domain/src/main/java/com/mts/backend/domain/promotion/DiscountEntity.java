package com.mts.backend.domain.promotion;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.promotion.identifier.DiscountId;
import com.mts.backend.domain.promotion.validator.IValidDateRange;
import com.mts.backend.domain.promotion.validator.IValidMaxUseDiscount;
import com.mts.backend.domain.promotion.value_object.DiscountName;
import com.mts.backend.domain.promotion.value_object.PromotionDiscountValue;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "Discount", schema = "milk_tea_shop_prod")
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@Builder
@Getter
@Setter
public class DiscountEntity extends BaseEntity<DiscountId> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã định danh duy nhất cho chương trình giảm giá")
    @Column(name = "discount_id", columnDefinition = "int UNSIGNED")
    @NotNull
    @Convert(converter = DiscountId.DiscountIdConverter.class)
    private DiscountId id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Liên kết với mã giảm giá (coupon), NULL nếu không yêu cầu mã giảm giá")
    @JoinColumn(name = "coupon_id", nullable = false)

    private CouponEntity couponEntity;

    @Comment("Số lượng sản phẩm tối thiểu cần mua để khuyến mãi")
    @ColumnDefault("'1'")
    @Column(name = "min_required_product", columnDefinition = "smallint UNSIGNED")
    @org.springframework.lang.Nullable
    @Positive(message = "Số lượng sản phẩm tối thiểu phải lớn hơn 0")
    private Integer minRequiredProduct;
    
    
    @IValidDateRange
    @IValidMaxUseDiscount
    public DiscountEntity(@NotNull DiscountId id, CouponEntity couponEntity, @org.springframework.lang.Nullable @Positive(message = "Số lượng sản phẩm tối thiểu phải lớn hơn 0") Integer minRequiredProduct, PromotionDiscountValue promotionDiscountValue, @Nullable @FutureOrPresent(message = "Thời gian bắt đầu hiệu lực phải lớn hơn thời gian hiện tại") LocalDateTime validFrom, @NotNull(message = "Thời gian kết thúc hiệu lực không được để trống") @Future(message = "Thời gian kết thúc hiệu lực phải lớn hơn thời gian hiện tại") LocalDateTime validUntil, Long currentUses, @Nullable @Positive(message = "Số lần sử dụng tối đa phải lớn hơn 0") Long maxUse, @org.springframework.lang.Nullable @Positive(message = "Số lần sử dụng tối đa mỗi khách hàng phải lớn hơn 0") Integer maxUsesPerCustomer, Boolean active, @NotNull DiscountName name, String description, Money minRequiredOrderValue) {
        this.id = id;
        this.couponEntity = couponEntity;
        this.minRequiredProduct = minRequiredProduct;
        this.promotionDiscountValue = promotionDiscountValue;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.currentUses = currentUses;
        this.maxUse = maxUse;
        this.maxUsesPerCustomer = maxUsesPerCustomer;
        this.active = active;
        this.name = name;
        this.description = description;
        this.minRequiredOrderValue = minRequiredOrderValue;
    }

    public DiscountEntity() {
    }

    public Optional<Integer> getMinRequiredProduct() {
        return Optional.ofNullable(minRequiredProduct);
    }

    public boolean changeMinRequiredProduct(Integer minRequiredProduct) {
        if (Objects.equals(this.minRequiredProduct, minRequiredProduct)) {
            return false;
        }

        this.minRequiredProduct = minRequiredProduct;
        return true;
    }

    @Getter
    @Embedded
    @AttributeOverrides(
            {
                    @AttributeOverride(name = "value", column = @Column(name = "discount_value", nullable = false,
                            precision = 11, scale = 3)),
                    @AttributeOverride(name = "unit", column = @Column(name = "discount_type", nullable = false)),
                    @AttributeOverride(name = "maxDiscountAmount", column = @Column(name = "max_discount_amount",
                            nullable = false, precision = 11, scale = 3))
            }
    )
    private PromotionDiscountValue promotionDiscountValue;

    public boolean changePromotionDiscountValue(PromotionDiscountValue promotionDiscountValue) {
        if (this.promotionDiscountValue.equals(promotionDiscountValue)) {
            return false;
        }

        this.promotionDiscountValue = promotionDiscountValue;
        return true;
    }


    @Comment("Thời điểm bắt đầu hiệu lực của chương trình giảm giá")
    @Column(name = "valid_from")
    @Nullable
    @FutureOrPresent(message = "Thời gian bắt đầu hiệu lực phải lớn hơn thời gian hiện tại")
    private LocalDateTime validFrom;

    public Optional<LocalDateTime> getValidFrom() {
        return Optional.ofNullable(validFrom);
    }
    @IValidDateRange
    public boolean changeValidFrom(LocalDateTime validFrom) {
        if (Objects.equals(this.validFrom, validFrom)) {
            return false;
        }
        this.validFrom = validFrom;
        return true;
    }


    @Getter
    @Comment("Thời điểm kết thúc hiệu lực của chương trình giảm giá")
    @Column(name = "valid_until", nullable = false)
    @NotNull(message = "Thời gian kết thúc hiệu lực không được để trống")
    @Future(message = "Thời gian kết thúc hiệu lực phải lớn hơn thời gian hiện tại")
    private LocalDateTime validUntil;

    @IValidDateRange
    public boolean changeValidUntil(LocalDateTime validUntil) {
        if (Objects.equals(this.validUntil, validUntil)) {
            return false;
        }

        this.validUntil = validUntil;
        return true;
    }


    @Comment("Số lần đã sử dụng chương trình giảm giá này")
    @ColumnDefault("'0'")
    @Column(name = "current_uses", columnDefinition = "int UNSIGNED")
    private Long currentUses;

    @Comment("Số lần sử dụng tối đa cho chương trình giảm giá, NULL nếu không giới hạn")
    @Column(name = "max_uses", columnDefinition = "int UNSIGNED")
    @Nullable
    @Positive(message = "Số lần sử dụng tối đa phải lớn hơn 0")
    private Long maxUse;


    @Comment("Số lần tối đa mỗi khách hàng được sử dụng chương trình giảm giá này, NULL nếu không giới hạn")
    @Column(name = "max_uses_per_customer", columnDefinition = "smallint UNSIGNED")
    @org.springframework.lang.Nullable
    @Positive(message = "Số lần sử dụng tối đa mỗi khách hàng phải lớn hơn 0")
    private Integer maxUsesPerCustomer;
    
    public Optional<Integer> getMaxUsesPerCustomer() {
        return Optional.ofNullable(maxUsesPerCustomer);
    }
    
    public Optional<Long> getMaxUse() {
        return Optional.ofNullable(maxUse);
    }
    @IValidMaxUseDiscount
    public boolean changeMaxUsesPerCustomer(Integer maxUsesPerCustomer) {
        if (Objects.equals(this.maxUsesPerCustomer, maxUsesPerCustomer)) {
            return false;
        }

        this.maxUsesPerCustomer = maxUsesPerCustomer;
        return true;
    }
    @IValidMaxUseDiscount
    public boolean changeMaxUse(Long maxUse) {
        if (Objects.equals(this.maxUse, maxUse)) {
            return false;
        }

        this.maxUse = maxUse;
        return true;
    }

    @Comment("Trạng thái kích hoạt: 1 - đang hoạt động, 0 - không hoạt động")
    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean active = false;


    @Column(name = "name", nullable = false, length = 500)
    @Convert(converter = DiscountName.DiscountNameConverter.class)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @NotNull
    @Getter
    private DiscountName name;

    public boolean changeDiscountName(DiscountName discountName) {
        if (this.name.equals(discountName)) {
            return false;
        }

        this.name = discountName;
        return true;
    }

    @Column(name = "description", length = 1000)
    @Getter
    private String description;

    public boolean changeDescription(String description) {
        if (Objects.equals(this.description, description)) {
            return false;
        }

        this.description = description;
        return true;
    }


    @Comment("Gái trị đơn hàng tối thiểu có thể áp dụng")
    @Column(name = "min_required_order_value", nullable = false, precision = 11, scale = 3)
    @Convert(converter = Money.MoneyConverter.class)
    @Getter
    private Money minRequiredOrderValue;

    public boolean changeMinRequiredOrderValue(Money minRequiredOrderValue) {
        if (this.minRequiredOrderValue.equals(minRequiredOrderValue)) {
            return false;
        }

        this.minRequiredOrderValue = minRequiredOrderValue;
        return true;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        DiscountEntity that = (DiscountEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
