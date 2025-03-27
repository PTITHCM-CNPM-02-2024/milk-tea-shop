package com.mts.backend.domain.persistence.entity;

import com.mts.backend.domain.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "Product", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "category_id", columnList = "category_id")
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductEntity extends BaseEntity<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã sản phẩm")
    @Column(name = "product_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã danh mục")
    @JoinColumn(name = "category_id")
    private CategoryEntity categoryEntity;

    @Comment("Tên sản phẩm")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Comment("Mô tả sản phẩm")
    @Column(name = "description", length = 1000)
    private String description;

    @Comment("Sản phẩm có sẵn (1: Có, 0: Không)")
    @ColumnDefault("1")
    @Column(name = "is_available")
    private Boolean isAvailable;

    @Comment("Sản phẩm đặc trưng (1: Có, 0: Không)")
    @ColumnDefault("0")
    @Column(name = "is_signature")
    private Boolean isSignature;

    @Comment("Đường dẫn mô tả hình ảnh")
    @Column(name = "image_path", length = 1000)
    private String imagePath;

}