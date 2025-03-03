package com.ptithcm.infrastructure.persistence.entity;

import com.ptithcm.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "StoreHour", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "store_id", columnList = "store_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "StoreHour_pk", columnNames = {"store_id", "day_of_week"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class StoreHour extends BaseEntity <Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Mã giờ mở cửa hàng")
    @Column(name = "store_hour_id", columnDefinition = "smallint UNSIGNED not null")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã cửa hàng")
    @JoinColumn(name = "store_id")
    private Store store;

    @Comment("Thứ trong tuần (1-7, 1: Chủ nhật)")
    @Column(name = "day_of_week", columnDefinition = "tinyint UNSIGNED not null")
    private Short dayOfWeek;

    @Comment("Giờ mở cửa")
    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @Comment("Giờ đóng cửa")
    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

}