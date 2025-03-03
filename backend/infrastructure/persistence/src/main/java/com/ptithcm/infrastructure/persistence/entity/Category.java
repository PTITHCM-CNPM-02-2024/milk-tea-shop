package com.ptithcm.infrastructure.persistence.entity;

import com.ptithcm.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "Category", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "Category_pk", columnNames = {"name"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class Category extends BaseEntity<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã danh mục")
    @Column(name = "category_id", columnDefinition = "smallint UNSIGNED not null")
    private Integer id;

    @Comment("Tên danh mục")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Comment("Mô tả danh mục")
    @Column(name = "description", length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã danh mục sản phẩm cha")
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

}