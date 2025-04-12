package com.mts.backend.domain.promotion;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.promotion.identifier.DiscountId;
import com.mts.backend.domain.promotion.validator.IValidDateRange;
import com.mts.backend.domain.promotion.validator.IValidMaxUseDiscount;
import com.mts.backend.domain.promotion.value_object.DiscountName;
import com.mts.backend.domain.promotion.value_object.PromotionDiscountValue;
import com.mts.backend.shared.exception.DomainException;
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
@Table(name = "discount", schema = "milk_tea_shop_prod")
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@Builder
@Getter
@Setter
public class DiscountEntity extends BaseEntity<Long> {

    @Id
    @Comment("Mã định danh duy nhất cho chương trình giảm giá")
    @Column(name = "discount_id", columnDefinition = "int UNSIGNED")
    @NotNull
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @Comment("Liên kết với mã giảm giá (coupon), NULL nếu không yêu cầu mã giảm giá")
    @JoinColumn(name = "coupon_id", nullable = false)
    private CouponEntity couponEntity;

    @Comment("Số lượng sản phẩm tối thiểu cần mua để khuyến mãi")
    @ColumnDefault("'1'")
    @Column(name = "min_required_product", columnDefinition = "smallint UNSIGNED")
    @org.springframework.lang.Nullable
    @Positive(message = "Số lượng sản phẩm tối thiểu phải lớn hơn 0")
    private Integer minRequiredProduct;
    
    
    public DiscountEntity(Long id, CouponEntity couponEntity, @org.springframework.lang.Nullable @Positive(message = "Số lượng sản phẩm tối thiểu phải lớn hơn 0") Integer minRequiredProduct, PromotionDiscountValue promotionDiscountValue, @Nullable  LocalDateTime validFrom, @NotNull(message = "Thời gian kết thúc hiệu lực không được để trống") LocalDateTime validUntil, Long currentUses, @Nullable @Positive(message = "Số lần sử dụng tối đa phải lớn hơn 0") Long maxUse, @org.springframework.lang.Nullable @Positive(message = "Số lần sử dụng tối đa mỗi khách hàng phải lớn hơn 0") Integer maxUsesPerCustomer, Boolean active, @NotNull DiscountName name, String description, Money minRequiredOrderValue) {
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
    private LocalDateTime validFrom;

    public Optional<LocalDateTime> getValidFrom() {
        return Optional.ofNullable(validFrom);
    }


    @Getter
    @Comment("Thời điểm kết thúc hiệu lực của chương trình giảm giá")
    @Column(name = "valid_until", nullable = false)
    @NotNull(message = "Thời gian kết thúc hiệu lực không được để trống")
    private LocalDateTime validUntil;
    

    @Comment("Số lần đã sử dụng chương trình giảm giá này")
    @ColumnDefault("'0'")
    @Column(name = "current_uses", columnDefinition = "int UNSIGNED")
    @NotNull(message = "Số lần sử dụng không được để trống")
    private Long currentUses;
    
    public long increaseCurrentUses() {
        // Kiểm tra xem có thể tăng thêm không
        if (this.getMaxUse().isPresent() && currentUses >= this.getMaxUse().get()) {
            throw new DomainException("Chương trình giảm giá đã đạt đến số lần sử dụng tối đa");
        }

        // Tăng số lần sử dụng
        currentUses = currentUses + 1;

        // Nếu sau khi tăng, đạt đến số lần sử dụng tối đa, tự động vô hiệu hóa
        if (maxUse != null && currentUses >= maxUse) {
            active = false;
        }
        
        return currentUses;
    }

    /**
     * Kiểm tra xem chương trình giảm giá còn lượt sử dụng không
     * @return true nếu còn lượt sử dụng, false nếu đã hết
     */
    public boolean hasRemainingUses() {
        if (maxUse == null) {
            return true; // Không giới hạn số lần sử dụng
        }
        return currentUses < maxUse;
    }

    /**
     * Lấy số lượt sử dụng còn lại
     * @return Optional chứa số lượt sử dụng còn lại, hoặc Empty nếu không có giới hạn
     */
    @Transient
    public Optional<Long> getRemainingUses() {
        if (maxUse == null) {
            return Optional.empty(); // Không giới hạn
        }
        long remaining = Math.max(0, maxUse - currentUses);
        return Optional.of(remaining);
    }

    /**
     * Kiểm tra xem khuyến mãi có đang trong thời gian hiệu lực không
     * @return true nếu đang trong thời gian hiệu lực, false nếu không
     */
    public boolean isInValidTimeRange() {
        LocalDateTime now = LocalDateTime.now();

        // Nếu chưa đến thời gian bắt đầu
        if (this.getValidFrom().isPresent() && now.isBefore(validFrom)) {
            return false;
        }

        // Nếu đã quá thời gian kết thúc
        return !now.isAfter(validUntil);
    }

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
    public boolean changeMaxUsesPerCustomer(Integer maxUsesPerCustomer) {
        if (Objects.equals(this.maxUsesPerCustomer, maxUsesPerCustomer)) {
            return false;
        }

        this.maxUsesPerCustomer = maxUsesPerCustomer;
        return true;
    }
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
    private Boolean active;


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

    /**
     * Cập nhật trạng thái active dựa trên thời gian hiệu lực hiện tại
     * @return true nếu trạng thái thay đổi, false nếu không thay đổi
     */
    public boolean updateActiveStatusBasedOnTime() {
        boolean shouldBeActive = isInValidTimeRange();

        // Chỉ cập nhật nếu trạng thái active hiện tại khác với trạng thái mong muốn
        if (this.active != shouldBeActive) {
            this.active = shouldBeActive;
            return true;
        }

        return false;
    }

    // Cập nhật phương thức changeValidFrom để xử lý trạng thái active
    @IValidDateRange(message = "Thời gian bắt đầu phải nhỏ hơn thời gian kết thúc")
    public boolean changeValidFrom(LocalDateTime validFrom) {
        if (Objects.equals(this.validFrom, validFrom)) {
            return false;
        }
        
        this.validFrom = validFrom;

        // Cập nhật trạng thái active dựa trên thời gian mới
        updateActiveStatusBasedOnTime();

        return true;
    }

    // Cập nhật phương thức changeValidUntil để xử lý trạng thái active
    public boolean changeValidUntil(LocalDateTime validUntil) {
        Objects.requireNonNull(validUntil, "Thời gian kết thúc không được để trống");
        if (this.validUntil.equals(validUntil)) {
            return false;
        }

        // Kiểm tra validUntil so với validFrom
        if (validFrom != null && validUntil.isBefore(validFrom)) {
            throw new IllegalArgumentException("Thời gian kết thúc không thể trước thời gian bắt đầu");
        }

        this.validUntil = validUntil;

        // Cập nhật trạng thái active dựa trên thời gian mới
        updateActiveStatusBasedOnTime();

        return true;
    }

    /**
     * Kiểm tra xem khuyến mãi có thể áp dụng được không
     * @return true nếu khuyến mãi có thể áp dụng, false nếu không
     */
    public boolean isApplicable() {
        // Kiểm tra trạng thái kích hoạt
        if (!active) {
            return false;
        }

        // Kiểm tra thời gian hiệu lực
        if (!isInValidTimeRange()) {
            return false;
        }

        // Kiểm tra số lần sử dụng tối đa
        if (!hasRemainingUses()) {
            return false;
        }

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
