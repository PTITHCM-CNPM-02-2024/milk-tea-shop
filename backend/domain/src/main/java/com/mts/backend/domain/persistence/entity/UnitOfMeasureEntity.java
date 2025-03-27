package com.mts.backend.domain.persistence.entity;

import com.mts.backend.domain.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitOfMeasureEntity extends BaseEntity <Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã đơn vị tính")
    @Column(name = "unit_id", columnDefinition = "smallint UNSIGNED")
    private Integer id;

    @Comment("Tên đơn vị tính (cái, cc, ml, v.v.)")
    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Comment("Ký hiệu (cc, ml, v.v.)")
    @Column(name = "symbol", length = 5, nullable = false)
    private String symbol;

    @Comment("Mô tả")
    @Column(name = "description", length = 1000)
    private String description;

}