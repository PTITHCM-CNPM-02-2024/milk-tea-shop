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
@Table(name = "MembershipType", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "MembershipType_pk", columnNames = {"type"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipTypeEntity extends BaseEntity <Integer> {
    @Id
    @Comment("Mã loại thành viên")
    @Column(name = "membership_type_id", columnDefinition = "tinyint UNSIGNED")
    private Integer id;
    

    @Comment("Giá trị giảm giá")
    @Column(name = "discount_value", nullable = false, precision = 10, scale = 3)
    private BigDecimal discountValue;

    @Comment("Đơn vị giảm giá (PERCENT, FIXED)")
    @Column(name = "discount_unit", nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountUnit discountUnit;
    
    @Comment("Điểm yêu cầu")
    @Column(name = "required_point", nullable = false)
    private Integer requiredPoint;

    @Comment("Mô tả")
    @Column(name = "description")
    private String description;

    @Comment("Ngày hết hạn")
    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    @Comment("Trạng thái (1: Hoạt động, 0: Không hoạt động)")
    @ColumnDefault("1")
    @Column(name = "is_active")
    private Boolean isActive;
    
    @Comment("Loại thành viên")
    @Column(name = "type", nullable = false, length = 50)
    private String type;

}