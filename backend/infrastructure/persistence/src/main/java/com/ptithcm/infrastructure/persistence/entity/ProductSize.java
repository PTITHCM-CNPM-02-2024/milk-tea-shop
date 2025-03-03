package com.ptithcm.infrastructure.persistence.entity;

import com.ptithcm.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
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
public class ProductSize extends BaseEntity<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã kích thước")
    @Column(name = "size_id", columnDefinition = "smallint UNSIGNED not null")
    private Integer id;

    @Comment("Tên kích thước (ví dụ: S, M, L)")
    @Column(name = "name", nullable = false, length = 5)
    private String name;

    @Comment("Mô tả")
    @Lob
    @Column(name = "description")
    private String description;

}