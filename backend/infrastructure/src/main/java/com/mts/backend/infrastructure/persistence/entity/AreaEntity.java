package com.mts.backend.infrastructure.persistence.entity;

import com.mts.backend.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "Area", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "Area_pk", columnNames = {"name"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class AreaEntity extends BaseEntity<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã khu vực")
    @Column(name = "area_id", columnDefinition = "smallint UNSIGNED")
    private Integer id;

    @Comment("Tên khu vực")
    @Column(name = "name", nullable = false, length = 3)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String name;

    @Comment("Mô tả")
    @Column(name = "description")
    private String description;

    @Comment("Số bàn tối đa")
    @Column(name = "max_tables", nullable = false)
    private Integer maxTables;

    @Comment("Trạng thái hoạt động (1: Có, 0: Không)")
    @ColumnDefault("1")
    @Column(name = "is_active")
    private Boolean isActive;

}