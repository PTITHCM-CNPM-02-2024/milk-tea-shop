package com.mts.backend.domain.product;

import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.product.identifier.CategoryId;
import com.mts.backend.domain.product.value_object.CategoryName;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.lang.Nullable;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;


@Entity
@Table(name = "category", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "category_pk", columnNames = {"name"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseEntity<Integer> {
    @Id
    @Comment("Mã danh mục")
    @Column(name = "category_id", columnDefinition = "smallint UNSIGNED")
    @NotNull
    @Getter
    private Integer id;

    private static Set<Product> $default$products() {
        return new LinkedHashSet<>();
    }

    public static CategoryBuilder builder() {
        return new CategoryBuilder();
    }

    public boolean setId(@NotNull CategoryId id) {
        if (CategoryId.of(this.id).equals(id)) {
            return false;
        }
        this.id = id.getValue();
        return true;
    }

    @Comment("Tên danh mục")
    @Column(name = "name", nullable = false, length = 100, unique = true, columnDefinition = "varchar(100)")
    @NotNull
    @Size(max = 100, message = "Tên danh mục không được vượt quá 100 ký tự")
    @jakarta.validation.constraints.NotBlank(message = "Tên danh mục không được để trống")
    private String name;

    public CategoryName getName() {
        return CategoryName.of(name);
    }

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private Set<Product> products = new LinkedHashSet<>();

    public Set<Product> getProducts() {
        return Set.copyOf(products);
    }

    public boolean setName(@NotNull CategoryName name) {
        if (CategoryName.of(this.name).equals(name)) {
            return false;
        }

        this.name = name.getValue();
        return true;
    }

    @Comment("Mô tả danh mục")
    @Column(name = "description", length = 1000)
    @Nullable
    @Setter
    private String description;

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }


    public static class CategoryBuilder {
        private @NotNull Integer id;
        private @NotNull
        @Size(max = 100, message = "Tên danh mục không được vượt quá 100 ký tự")
        @NotBlank(message = "Tên danh mục không được để trống") String name;
        private Set<Product> products$value;
        private boolean products$set;
        private String description;

        CategoryBuilder() {
        }

        public CategoryBuilder id(@NotNull Integer id) {
            this.id = id;
            return this;
        }

        public CategoryBuilder name(@NotNull CategoryName name) {
            this.name = name.getValue();
            return this;
        }

        public CategoryBuilder products(Set<Product> products) {
            this.products$value = products;
            this.products$set = true;
            return this;
        }

        public CategoryBuilder description(@Nullable String description) {
            this.description = description;
            return this;
        }

        public Category build() {
            Set<Product> products$value = this.products$value;
            if (!this.products$set) {
                products$value = Category.$default$products();
            }
            return new Category(this.id, this.name, products$value, this.description);
        }

        public String toString() {
            return "Category.CategoryBuilder(id=" + this.id + ", name=" + this.name + ", products$value=" + this.products$value + ", description=" + this.description + ")";
        }
    }
}