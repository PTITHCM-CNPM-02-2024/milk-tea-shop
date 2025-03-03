package com.ptithcm.infrastructure.persistence.entity;

import com.ptithcm.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "UnitOfMeasure", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "UnitOfMeasure_pk", columnNames = {"name"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class UnitOfMeasure extends BaseEntity <Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã đơn vị tính")
    @Column(name = "unit_id", columnDefinition = "smallint UNSIGNED not null")
    private Integer id;

    @Comment("Tên đơn vị tính (cái, cc, ml, v.v.)")
    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Comment("Ký hiệu (cc, ml, v.v.)")
    @Column(name = "symbol", length = 10)
    private String symbol;

    @Comment("Mô tả")
    @Lob
    @Column(name = "description")
    private String description;

}