package com.ptithcm.infrastructure.persistence.entity;

import com.ptithcm.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "ExpenseCategory", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "ExpenseCategory_pk", columnNames = {"name"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class ExpenseCategory extends BaseEntity<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã loại chi phí")
    @Column(name = "expense_category_id", columnDefinition = "smallint UNSIGNED not null")
    private Integer id;

    @Comment("Tên loại chi phí (ví dụ: Nguyên liệu, Tiền điện, Lương nhân viên)")
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Comment("Mô tả")
    @Lob
    @Column(name = "description")
    private String description;

}