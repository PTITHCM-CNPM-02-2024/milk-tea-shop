package com.ptithcm.infrastructure.persistence.entity;

import com.ptithcm.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "IncomeCategory", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "IncomeCategory_pk", columnNames = {"name"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class IncomeCategory extends BaseEntity <Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã loại thu nhập")
    @Column(name = "income_category_id", columnDefinition = "smallint UNSIGNED not null")
    private Integer id;

    @Comment("Tên loại thu nhập (ví dụ: Bán hàng, Hoàn tiền)")
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Comment("Mô tả")
    @Lob
    @Column(name = "description")
    private String description;

}