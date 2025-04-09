package com.mts.backend.domain.product;

import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.product.value_object.CategoryName;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.lang.Nullable;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "category", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "category_pk", columnNames = {"name"})
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

    @OneToMany(mappedBy = "categoryEntity", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ProductEntity> products = new LinkedHashSet<>();
    
    public Set<ProductEntity> getProducts() {
        return Set.copyOf(products);
    }

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


}