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
@Table(name = "MembershipType", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "MembershipType_pk", columnNames = {"type"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class MembershipType extends BaseEntity <Short> {
    @Id
    @Comment("Mã loại thành viên")
    @Column(name = "membership_type_id", columnDefinition = "tinyint UNSIGNED not null")
    private Short id;

    @Comment("Loại thành viên")
    @Lob
    @Column(name = "type", nullable = false)
    private String type;

    @Comment("Giá trị giảm giá")
    @Column(name = "discount_value", nullable = false, precision = 10, scale = 3)
    private BigDecimal discountValue;

    @Comment("Đơn vị giảm giá (PERCENT, FIXED)")
    @Lob
    @Column(name = "discount_unit", nullable = false)
    private String discountUnit;

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

}