package com.ptithcm.infrastructure.persistence.entity;

import com.ptithcm.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
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
public class Discount extends BaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã định danh duy nhất cho chương trình giảm giá")
    @Column(name = "discount_id", columnDefinition = "int UNSIGNED not null")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Liên kết với bảng DiscountType, xác định loại giảm giá (ví dụ: giảm giá theo mùa, khuyến mãi đặc biệt, v.v.)")
    @JoinColumn(name = "discount_type_id", nullable = false)
    private com.ptithcm.infrastructure.persistence.entity.DiscountType discountType;

    @Comment("Liên kết với mã giảm giá (coupon), NULL nếu không yêu cầu mã giảm giá")
    @Column(name = "coupon_id", columnDefinition = "int UNSIGNED")
    private Long couponId;

    @Comment("Cách áp dụng khi có nhiều giảm giá: BEST - chọn giảm giá tốt nhất, COMBINE - kết hợp các giảm giá")
    @Lob
    @Column(name = "apply_type", nullable = false)
    private String applyType;

    @Comment("Giá trị giảm giá (phần trăm hoặc số tiền cố định)")
    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Comment("Giới hạn số tiền giảm giá tối đa, NULL nếu không giới hạn")
    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Comment("Thời điểm bắt đầu hiệu lực của chương trình giảm giá")
    @Column(name = "valid_from", nullable = false)
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
    @Column(name = "is_active")
    private Boolean isActive;

}