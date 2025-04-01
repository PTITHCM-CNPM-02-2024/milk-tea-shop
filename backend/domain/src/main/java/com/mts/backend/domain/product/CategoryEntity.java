package com.mts.backend.domain.product;

import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.product.value_object.CategoryName;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.lang.Nullable;

import java.util.Optional;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryEntity extends BaseEntity<Integer> {
    @Id
    @Comment("Mã danh mục")
    @Column(name = "category_id", columnDefinition = "smallint UNSIGNED")
    @NotNull
    private Integer id;

    @Comment("Tên danh mục")
    @Column(name = "name", nullable = false, length = 100, unique = true, columnDefinition = "varchar(100)")
    @Convert(converter = CategoryName.CategoryNameConverter.class)
    @NotNull
    private CategoryName name;
    
    public boolean changeName(CategoryName name){
        if(this.name.equals(name)){
            return false;
        }
        
        this.name = name;
        return true;
    }

    @Comment("Mô tả danh mục")
    @Column(name = "description", length = 1000)
    @Nullable
    private String description;
    
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã danh mục sản phẩm cha")
    @JoinColumn(name = "parent_category_id")
    @Nullable
    private CategoryEntity parentCategoryEntity;
    
    public Optional<CategoryEntity> getParentCategoryEntity(){
        return Optional.ofNullable(this.parentCategoryEntity);
    }

    

}