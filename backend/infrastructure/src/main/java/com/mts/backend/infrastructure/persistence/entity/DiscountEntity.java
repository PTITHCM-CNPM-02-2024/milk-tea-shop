package com.mts.backend.infrastructure.persistence.entity;

import com.mts.backend.domain.common.value_object.DiscountUnit;
import com.mts.backend.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Discount", schema = "milk_tea_shop_prod")
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountEntity extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã định danh duy nhất cho chương trình giảm giá")
    @Column(name = "discount_id", columnDefinition = "int UNSIGNED")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @Comment("Liên kết với mã giảm giá (coupon), NULL nếu không yêu cầu mã giảm giá")
    @JoinColumn(name = "coupon_id", nullable = false)
    private CouponEntity couponEntity;

    @Comment("Giá trị giảm giá (phần trăm hoặc số tiền cố định)")
    @Column(name = "discount_value", nullable = false, precision = 11, scale = 3)
    private BigDecimal discountValue;

    @Comment("Giới hạn số tiền giảm giá tối đa, NULL nếu không giới hạn")
    @Column(name = "max_discount_amount", nullable = false, precision = 11, scale = 3)
    private BigDecimal maxDiscountAmount;

    @Comment("Số lượng sản phẩm tối thiểu cần mua để khuyến mãi")
    @ColumnDefault("'1'")
    @Column(name = "min_required_product", columnDefinition = "smallint UNSIGNED")
    private Integer minRequiredProduct;


    @Comment("Thời điểm bắt đầu hiệu lực của chương trình giảm giá")
    @Column(name = "valid_from")
    private LocalDateTime validFrom;


    @Comment("Thời điểm kết thúc hiệu lực của chương trình giảm giá")
    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;


    @Comment("Số lần đã sử dụng chương trình giảm giá này")
    @ColumnDefault("'0'")
    @Column(name = "current_uses", columnDefinition = "int UNSIGNED")
    private Long currentUses;

    @Comment("Số lần sử dụng tối đa cho chương trình giảm giá, NULL nếu không giới hạn")
    @Column(name = "max_uses", columnDefinition = "int UNSIGNED")
    private Long maxUses;


    @Comment("Số lần tối đa mỗi khách hàng được sử dụng chương trình giảm giá này, NULL nếu không giới hạn")
    @Column(name = "max_uses_per_customer", columnDefinition = "smallint UNSIGNED")
    private Integer maxUsesPerCustomer;

    @Comment("Trạng thái kích hoạt: 1 - đang hoạt động, 0 - không hoạt động")
    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    
    @Column(name="discount_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountUnit discountUnit;

    @Column(name = "name", nullable = false, length = 500)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Comment("Gái trị đơn hàng tối thiểu có thể áp dụng")
    @Column(name = "min_required_order_value", nullable = false, precision = 11, scale = 3)
    private BigDecimal minRequiredOrderValue;

}
