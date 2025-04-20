package com.mts.backend.domain.order;

import com.mts.backend.domain.order.identifier.OrderTableId;
import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.store.ServiceTable;
import com.mts.backend.shared.exception.DomainException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "order_table", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "order_id", columnList = "order_id"),
        @Index(name = "table_id", columnList = "table_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "order_table_pk", columnNames = {"order_id", "table_id"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@AllArgsConstructor
@NoArgsConstructor
public class OrderTable extends BaseEntity<Long> {
    @Id
    @Comment("Mã đơn hàng và bàn")
    @Column(name = "order_table_id", columnDefinition = "int UNSIGNED")
    @NotNull
    @Getter
    private Long id;

    public static OrderTableBuilder builder() {
        return new OrderTableBuilder();
    }

    public boolean setId(@NotNull OrderTableId id) {
        if (OrderTableId.of(this.id).equals(id)) {
            return false;
        }
        this.id = id.getValue();
        return true;
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Mã đơn hàng")
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public boolean setOrder(@NotNull Order order) {
        if (Objects.equals(this.order, order)) {
            return false;
        }
        this.order = order;
        return true;
    }

    @Comment("Thời gian vào bàn")
    @Column(name = "check_in", nullable = false)
    @NotNull
    @Getter
    private LocalDateTime checkIn;

    @Comment("Thời gian rời bàn")
    @Column(name = "check_out")
    @Nullable
    @Getter
    private LocalDateTime checkOut;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Comment("Mã bàn")
    @JoinColumn(name = "table_id", nullable = false)
    @Getter
    private ServiceTable table;

    public boolean setTable(@NotNull ServiceTable table) {
        if (Objects.equals(this.table, table)) {
            return false;
        }
        this.table = table;
        return true;
    }

    public void setCheckIn(LocalDateTime checkIn) {
        if (this.checkIn == null) this.checkIn = checkIn;
    }

    public void setCheckOut(@NotNull LocalDateTime checkOut) {
        Objects.requireNonNull(checkOut, "checkOut must not be null");

        if (checkOut.isBefore(this.checkIn)) {
            throw new DomainException("Thời gian rời bàn không được nhỏ hơn thời gian vào bàn");
        }

        if (this.checkOut == null) {
            this.checkOut = checkOut;
        }

    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        OrderTable that = (OrderTable) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public static class OrderTableBuilder {
        private @NotNull Long id;
        private Order order;
        private @NotNull LocalDateTime checkIn;
        private LocalDateTime checkOut;
        private ServiceTable table;

        OrderTableBuilder() {
        }

        public OrderTableBuilder id(@NotNull Long id) {
            this.id = id;
            return this;
        }

        public OrderTableBuilder order(Order order) {
            this.order = order;
            return this;
        }

        public OrderTableBuilder checkIn(@NotNull LocalDateTime checkIn) {
            this.checkIn = checkIn;
            return this;
        }

        public OrderTableBuilder checkOut(LocalDateTime checkOut) {
            this.checkOut = checkOut;
            return this;
        }

        public OrderTableBuilder table(ServiceTable table) {
            this.table = table;
            return this;
        }

        public OrderTable build() {
            return new OrderTable(this.id, this.order, this.checkIn, this.checkOut, this.table);
        }

        public String toString() {
            return "OrderTable.OrderTableBuilder(id=" + this.id + ", order=" + this.order + ", checkIn=" + this.checkIn + ", checkOut=" + this.checkOut + ", table=" + this.table + ")";
        }
    }
}