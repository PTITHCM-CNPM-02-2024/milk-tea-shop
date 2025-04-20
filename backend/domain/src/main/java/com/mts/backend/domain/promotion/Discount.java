package com.mts.backend.domain.promotion;

import com.mts.backend.domain.common.value_object.Money;
import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.promotion.identifier.DiscountId;
import com.mts.backend.domain.promotion.value_object.DiscountName;
import com.mts.backend.domain.promotion.value_object.PromotionDiscountValue;
import com.mts.backend.shared.exception.DomainException;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "discount", schema = "milk_tea_shop_prod")
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class Discount extends BaseEntity<Long> {

    @Id
    @Comment("Mã định danh duy nhất cho chương trình giảm giá")
    @Column(name = "discount_id", columnDefinition = "int UNSIGNED")
    @NotNull
    @Getter
    private Long id;

    public Discount(@NotNull Long id, Coupon coupon, @org.springframework.lang.Nullable @Positive(message = "Số lượng sản phẩm tối thiểu phải lớn hơn 0") Integer minRequiredProduct, PromotionDiscountValue promotionDiscountValue, @Nullable LocalDateTime validFrom, @NotNull(message = "Thời gian kết thúc hiệu lực không được để trống") LocalDateTime validUntil, @NotNull(message = "Số lần sử dụng không được để trống") Long currentUse, @Nullable @Positive(message = "Số lần sử dụng tối đa phải lớn hơn 0") Long maxUse, @org.springframework.lang.Nullable @Positive(message = "Số lần sử dụng tối đa mỗi khách hàng phải lớn hơn 0") Integer maxUsesPerCustomer, Boolean active, @NotNull @Size(max = 500, message = "Tên khuyến mãi không được quá 500 ký tự") @NotBlank(message = "Tên khuyến mãi không được để trống") String name, String description, BigDecimal minRequiredOrderValue) {
        this.id = id;
        this.coupon = coupon;
        this.minRequiredProduct = minRequiredProduct;
        this.promotionDiscountValue = promotionDiscountValue;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.currentUse = currentUse;
        this.maxUse = maxUse;
        this.maxUsesPerCustomer = maxUsesPerCustomer;
        this.active = active;
        this.name = name;
        this.description = description;
        this.minRequiredOrderValue = minRequiredOrderValue;
    }

    public Discount() {
    }

    public static DiscountBuilder builder() {
        return new DiscountBuilder();
    }

    public boolean setId(@NotNull DiscountId discountId) {
        if (DiscountId.of(this.id).equals(discountId)) {
            return false;
        }

        this.id = discountId.getValue();
        return true;
    }

    @OneToOne(fetch = FetchType.EAGER, optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @Comment("Liên kết với mã giảm giá (coupon), NULL nếu không yêu cầu mã giảm giá")
    @JoinColumn(name = "coupon_id", nullable = false)
    @NotNull
    @Getter
    private Coupon coupon;
    
    public boolean setCoupon(@NotNull Coupon coupon) {
        if (this.coupon.equals(coupon)) {
            return false;
        }

        this.coupon = coupon;
        return true;
    }

    @Comment("Số lượng sản phẩm tối thiểu cần mua để khuyến mãi")
    @ColumnDefault("'1'")
    @Column(name = "min_required_product", columnDefinition = "smallint UNSIGNED")
    @org.springframework.lang.Nullable
    @Positive(message = "Số lượng sản phẩm tối thiểu phải lớn hơn 0")
    private Integer minRequiredProduct;

    public Optional<Integer> getMinRequiredProduct() {
        return Optional.ofNullable(minRequiredProduct);
    }

    public boolean setMinRequiredProduct(Integer minRequiredProduct) {
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

    public boolean setPromotionDiscountValue(@NotNull PromotionDiscountValue promotionDiscountValue) {
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
    @Getter
    private Long currentUse;

    public long increaseCurrentUses() {
        // Kiểm tra xem có thể tăng thêm không
        if (this.getMaxUse().isPresent() && currentUse >= this.getMaxUse().get()) {
            throw new DomainException("Chương trình giảm giá đã đạt đến số lần sử dụng tối đa");
        }

        // Tăng số lần sử dụng
        currentUse = currentUse + 1;

        // Nếu sau khi tăng, đạt đến số lần sử dụng tối đa, tự động vô hiệu hóa
        if (maxUse != null && currentUse >= maxUse) {
            active = false;
        }

        return currentUse;
    }

    /**
     * Kiểm tra xem chương trình giảm giá còn lượt sử dụng không
     *
     * @return true nếu còn lượt sử dụng, false nếu đã hết
     */
    public boolean hasRemainingUses() {
        if (maxUse == null) {
            return true; // Không giới hạn số lần sử dụng
        }
        return currentUse < maxUse;
    }

    /**
     * Lấy số lượt sử dụng còn lại
     *
     * @return Optional chứa số lượt sử dụng còn lại, hoặc Empty nếu không có giới hạn
     */
    @Transient
    public Optional<Long> getRemainingUses() {
        if (maxUse == null) {
            return Optional.empty(); // Không giới hạn
        }
        long remaining = Math.max(0, maxUse - currentUse);
        return Optional.of(remaining);
    }

    /**
     * Kiểm tra xem khuyến mãi có đang trong thời gian hiệu lực không
     *
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

    public boolean setMaxUsesPerCustomer(@Nullable Integer maxUsesPerCustomer) {
        if (Objects.equals(this.maxUsesPerCustomer, maxUsesPerCustomer)) {
            return false;
        }

        if (maxUsesPerCustomer != null && maxUse != null) {
            validate(maxUsesPerCustomer, maxUse);
        }
        
        this.maxUsesPerCustomer = maxUsesPerCustomer;
        return true;
    }

    public boolean setMaxUse(@Nullable Long maxUse) {
        if (Objects.equals(this.maxUse, maxUse)) {
            return false;
        }

        if (maxUse != null && currentUse != null) {
            validate(currentUse, maxUse);
        }

        if (maxUsesPerCustomer != null) {
            validate(maxUsesPerCustomer, maxUse);
        }

        this.maxUse = maxUse;
        return true;
    }
    
    
    @Comment("Trạng thái kích hoạt: 1 - đang hoạt động, 0 - không hoạt động")
    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    @Getter
    @Setter
    private Boolean active;


    @Column(name = "name", nullable = false, length = 500)
    @NotNull
    @Size(max = 500, message = "Tên khuyến mãi không được quá 500 ký tự")
    @NotBlank(message = "Tên khuyến mãi không được để trống")
    private String name;

    public boolean setName(@NotNull DiscountName discountName) {
        if (DiscountName.of(this.name).equals(discountName)) {
            return false;
        }

        this.name = discountName.getValue();
        return true;
    }

    public DiscountName getName() {
        return DiscountName.of(this.name);
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
    private BigDecimal minRequiredOrderValue;

    public Money getMinRequiredOrderValue() {
        return Money.of(minRequiredOrderValue);
    }

    public boolean setMinRequiredOrderValue(@NotNull Money minRequiredOrderValue) {
        if (Money.of(this.minRequiredOrderValue).equals(minRequiredOrderValue)) {
            return false;
        }

        this.minRequiredOrderValue = minRequiredOrderValue.getValue();
        return true;
    }

    /**
     * Cập nhật trạng thái active dựa trên thời gian hiệu lực hiện tại
     *
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
    public boolean setValidFrom(@Nullable LocalDateTime validFrom) {
        if (Objects.equals(this.validFrom, validFrom)) {
            return false;
        }

        if (validFrom != null) {
            validate(validFrom, validUntil);
        }

        this.validFrom = validFrom;

        // Cập nhật trạng thái active dựa trên thời gian mới
        updateActiveStatusBasedOnTime();

        return true;
    }

    // Cập nhật phương thức changeValidUntil để xử lý trạng thái active
    public boolean setValidUntil(@NotNull LocalDateTime validUntil) {
        if (this.validUntil.equals(validUntil)) {
            return false;
        }

        if (validFrom != null) {
            validate(validFrom, validUntil);
        }

        this.validUntil = validUntil;

        // Cập nhật trạng thái active dựa trên thời gian mới
        updateActiveStatusBasedOnTime();

        return true;
    }

    /**
     * Kiểm tra xem khuyến mãi có thể áp dụng được không
     *
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

    private void validate(LocalDateTime validFrom, LocalDateTime validUntil) {
        if (validFrom.isAfter(validUntil)) {
            throw new DomainException("Thời gian bắt đầu không được lớn hơn thời gian kết thúc");
        }
    }

    private void validate(Long currentUse, Long maxUse) {
        if (currentUse > maxUse) {
            throw new DomainException("Số lần sử dụng không được lớn hơn số lần sử dụng tối đa");
        }
    }

    private void validate(Integer maxUsesPerCustomer, Long maxUse) {
        if (maxUsesPerCustomer > maxUse) {
            throw new DomainException("Số lần sử dụng tối đa mỗi khách hàng không được lớn hơn số lần sử dụng tối đa");
        }
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Discount that = (Discount) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public static class DiscountBuilder {
        private @NotNull Long id;
        private Coupon coupon;
        private @Positive(message = "Số lượng sản phẩm tối thiểu phải lớn hơn 0") Integer minRequiredProduct;
        private PromotionDiscountValue promotionDiscountValue;
        private LocalDateTime validFrom;
        private @NotNull(message = "Thời gian kết thúc hiệu lực không được để trống") LocalDateTime validUntil;
        private @NotNull(message = "Số lần sử dụng không được để trống") Long currentUse;
        private @Positive(message = "Số lần sử dụng tối đa phải lớn hơn 0") Long maxUse;
        private @Positive(message = "Số lần sử dụng tối đa mỗi khách hàng phải lớn hơn 0") Integer maxUsesPerCustomer;
        private Boolean active;
        private @NotNull
        @Size(max = 500, message = "Tên khuyến mãi không được quá 500 ký tự")
        @NotBlank(message = "Tên khuyến mãi không được để trống") String name;
        private String description;
        private BigDecimal minRequiredOrderValue;

        DiscountBuilder() {
        }

        public DiscountBuilder id(@NotNull Long id) {
            this.id = id;
            return this;
        }

        public DiscountBuilder coupon(Coupon coupon) {
            this.coupon = coupon;
            return this;
        }

        public DiscountBuilder minRequiredProduct(@Positive(message = "Số lượng sản phẩm tối thiểu phải lớn hơn 0") Integer minRequiredProduct) {
            this.minRequiredProduct = minRequiredProduct;
            return this;
        }

        public DiscountBuilder promotionDiscountValue(@NotNull PromotionDiscountValue promotionDiscountValue) {
            this.promotionDiscountValue = promotionDiscountValue;
            return this;
        }

        public DiscountBuilder validFrom(LocalDateTime validFrom) {
            this.validFrom = validFrom;
            return this;
        }

        public DiscountBuilder validUntil(@NotNull(message = "Thời gian kết thúc hiệu lực không được để trống") LocalDateTime validUntil) {
            this.validUntil = validUntil;
            return this;
        }

        public DiscountBuilder currentUse(@NotNull(message = "Số lần sử dụng không được để trống") Long currentUse) {
            this.currentUse = currentUse;
            return this;
        }

        public DiscountBuilder maxUse(@Positive(message = "Số lần sử dụng tối đa phải lớn hơn 0") Long maxUse) {
            this.maxUse = maxUse;
            return this;
        }

        public DiscountBuilder maxUsesPerCustomer(@Positive(message = "Số lần sử dụng tối đa mỗi khách hàng phải lớn hơn 0") Integer maxUsesPerCustomer) {
            this.maxUsesPerCustomer = maxUsesPerCustomer;
            return this;
        }

        public DiscountBuilder active(Boolean active) {
            this.active = active;
            return this;
        }

        public DiscountBuilder name(@NotNull DiscountName name) {
            this.name = name.getValue();
            return this;
        }

        public DiscountBuilder description(String description) {
            this.description = description;
            return this;
        }

        public DiscountBuilder minRequiredOrderValue(@NotNull Money minRequiredOrderValue) {
            this.minRequiredOrderValue = minRequiredOrderValue.getValue();
            return this;
        }

        public Discount build() {
            return new Discount(this.id, this.coupon, this.minRequiredProduct, this.promotionDiscountValue, this.validFrom, this.validUntil, this.currentUse, this.maxUse, this.maxUsesPerCustomer, this.active, this.name, this.description, this.minRequiredOrderValue);
        }

        public String toString() {
            return "Discount.DiscountBuilder(id=" + this.id + ", coupon=" + this.coupon + ", minRequiredProduct=" + this.minRequiredProduct + ", promotionDiscountValue=" + this.promotionDiscountValue + ", validFrom=" + this.validFrom + ", validUntil=" + this.validUntil + ", currentUse=" + this.currentUse + ", maxUse=" + this.maxUse + ", maxUsesPerCustomer=" + this.maxUsesPerCustomer + ", active=" + this.active + ", name=" + this.name + ", description=" + this.description + ", minRequiredOrderValue=" + this.minRequiredOrderValue + ")";
        }
    }
}
