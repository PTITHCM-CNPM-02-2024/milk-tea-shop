package com.mts.backend.infrastructure.persistence.entity;

import com.mts.backend.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@Entity
@Table(name = "ServiceTable", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "area_id", columnList = "area_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "ServiceTable_pk", columnNames = {"area_id", "table_number"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTableEntity extends BaseEntity<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã bàn")
    @Column(name = "table_id", columnDefinition = "smallint UNSIGNED")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã khu vực")
    @JoinColumn(name = "area_id")
    private AreaEntity areaEntity;

    @Comment("Số bàn")
    @Column(name = "table_number", nullable = false, length = 10)
    private String tableNumber;

    @Comment("Bàn có sẵn (1: Có, 0: Không)")
    @ColumnDefault("1")
    @Column(name = "is_active")
    private Boolean isActive;

}