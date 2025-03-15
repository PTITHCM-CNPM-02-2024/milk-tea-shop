package com.mts.backend.infrastructure.persistence.entity;

import com.mts.backend.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "ProductSize", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "ProductSize_pk", columnNames = {"unit_id", "name"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSizeEntity extends BaseEntity<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã kích thước")
    @Column(name = "size_id", columnDefinition = "smallint UNSIGNED")
    private Integer id;

    @Comment("Tên kích thước (ví dụ: S, M, L)")
    @Column(name = "name", nullable = false, length = 5)
    private String name;
    
    @Comment("Số lượng")
    @Column(name = "quantity", columnDefinition = "smallint UNSIGNED")
    private Integer quantity;

    @Comment("Mô tả")
    @Column(name = "description", length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã đơn vị tính")
    @JoinColumn(name = "unit_id", nullable = false)
    private UnitOfMeasureEntity unit;

}