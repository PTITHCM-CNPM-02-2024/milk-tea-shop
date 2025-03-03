package com.ptithcm.infrastructure.persistence.entity;

import com.ptithcm.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "Material", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "Material_pk", columnNames = {"name"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class Material extends BaseEntity <Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã nguyên vật liệu")
    @Column(name = "material_id", nullable = false)
    private Integer id;

    @Comment("Tên nguyên vật liệu")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Comment("Mô tả")
    @Lob
    @Column(name = "description")
    private String description;

    @Comment("Đơn vị tính (e.g., kg, lít, cái)")
    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @Comment("Số lượng tối thiểu")
    @Column(name = "min_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal minQuantity;

    @Comment("Số lượng tối đa")
    @Column(name = "max_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal maxQuantity;

}