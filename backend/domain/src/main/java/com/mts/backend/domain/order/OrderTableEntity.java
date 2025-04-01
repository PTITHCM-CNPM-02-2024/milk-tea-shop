package com.mts.backend.domain.order;

import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.store.ServiceTableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "OrderTable", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "order_id", columnList = "order_id"),
        @Index(name = "table_id", columnList = "table_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "OrderTable_pk", columnNames = {"order_id", "table_id"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@Builder
public class OrderTableEntity extends BaseEntity<Long> {
    @Id
    @Comment("Mã đơn hàng và bàn")
    @Column(name = "order_table_id", columnDefinition = "int UNSIGNED")
    @NotNull
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Mã đơn hàng")
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity orderEntity;

    @Comment("Thời gian vào bàn")
    @Column(name = "check_in", nullable = false)
    @NotNull
    private LocalDateTime checkIn;

    @Comment("Thời gian rời bàn")
    @Column(name = "check_out")
    private LocalDateTime checkOut;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Mã bàn")
    @JoinColumn(name = "table_id", nullable = false)
    @NotNull
    private ServiceTableEntity table;

    public OrderTableEntity(Long id, OrderEntity orderEntity, @NotNull LocalDateTime checkIn, LocalDateTime checkOut, ServiceTableEntity table) {
        this.id = id;
        this.orderEntity = orderEntity;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.table = table;
    }

    public OrderTableEntity() {
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        OrderTableEntity that = (OrderTableEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}