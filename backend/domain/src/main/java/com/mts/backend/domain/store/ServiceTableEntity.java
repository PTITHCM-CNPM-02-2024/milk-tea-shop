package com.mts.backend.domain.store;

import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.domain.store.value_object.TableNumber;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.Optional;

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
public class ServiceTableEntity extends BaseEntity<ServiceTableId> {
    @Id
    @Comment("Mã bàn")
    @Getter
    @Column(name = "table_id", columnDefinition = "smallint UNSIGNED")
    @Convert(converter = ServiceTableId.ServiceTableIdConverter.class)
    @NotNull
    private ServiceTableId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã khu vực")
    @JoinColumn(name = "area_id")
    @Nullable
    @Setter
    private AreaEntity areaEntity;

    @Comment("Số bàn")
    @Column(name = "table_number", nullable = false, length = 10)
    @NotNull
    @Convert(converter = TableNumber.TableNumberConverter.class)
    @Getter
    private TableNumber tableNumber;

    @Comment("Bàn có sẵn (1: Có, 0: Không)")
    @ColumnDefault("1")
    @Column(name = "is_active")
    @Getter
    private Boolean active;

    public ServiceTableEntity(ServiceTableId id, @Nullable AreaEntity areaEntity, @NotNull TableNumber tableNumber, Boolean active) {
        this.id = id;
        this.areaEntity = areaEntity;
        this.tableNumber = tableNumber;
        this.active = active;
    }

    public ServiceTableEntity() {
    }


    public Optional<AreaEntity> getAreaEntity() {
        return Optional.ofNullable(areaEntity);
    }

    public boolean changeTableNumber(TableNumber tableNumber) {
        if (this.tableNumber.equals(tableNumber)) {
            return false;
        }

        this.tableNumber = tableNumber;
        return true;
    }

    public void changeIsActive(boolean isActive) {
        this.active = isActive;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ServiceTableEntity that = (ServiceTableEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}