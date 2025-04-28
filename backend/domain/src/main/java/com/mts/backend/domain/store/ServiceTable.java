package com.mts.backend.domain.store;

import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.store.identifier.ServiceTableId;
import com.mts.backend.domain.store.value_object.TableNumber;
import com.mts.backend.shared.exception.DomainException;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "service_table", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "area_id", columnList = "area_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "service_table_pk", columnNames = {"area_id", "table_number"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class ServiceTable extends BaseEntity<Integer> {
    @Id
    @Comment("Mã bàn")
    @Getter
    @Column(name = "table_id", columnDefinition = "smallint UNSIGNED")
    @NotNull
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã khu vực")
    @JoinColumn(name = "area_id")
    @Nullable
    private Area area;
    @Comment("Số bàn")
    @Column(name = "table_number", nullable = false, length = 10)
    @NotNull
    @Size(max = 10, message = "Số bàn không được vượt quá 10 ký tự")
    @NotBlank(message = "Số bàn không được để trống")
    private String tableNumber;
    @Comment("Bàn có sẵn (1: Có, 0: Không)")
    @ColumnDefault("1")
    @Column(name = "is_active")
    @Getter
    private Boolean active;

    public ServiceTable(@NotNull Integer id, @Nullable Area area, @NotNull @Size(max = 10, message = "Số bàn không được vượt quá 10 ký tự") @NotBlank(message = "Số bàn không được để trống") String tableNumber, Boolean active) {
        this.id = id;
        this.area = area;
        this.tableNumber = tableNumber;
        this.active = active;
        
        validate(active, area);
    }

    public ServiceTable() {
    }

    public void setActive(@NotNull Boolean active) {
        this.active = active;
        validate(active, this.area);
    }


    public static ServiceTableBuilder builder() {
        return new ServiceTableBuilder();
    }

    private void validate(Boolean active, Area area) {
        if (area == null) {
            return;
        }
        if (active && !area.getActive()) {
            throw new DomainException("Khu vực" + area.getName().getValue() + " không hoạt động, không thể kích hoạt bàn");
        }
    }

    public boolean setId(@NotNull ServiceTableId serviceTableId) {
        if (ServiceTableId.of(this.id).equals(serviceTableId)) {
            return false;
        }

        this.id = serviceTableId.getValue();
        return true;
    }

    public boolean setArea(@Nullable Area area) {
        if (this.getArea().isPresent() && Objects.equals(this.getArea().get(), area)) {
            return false;
        }
        if (area != null && area.getMaxTable().isPresent() && area.getServiceTables().size() >= area.getMaxTable().get().getValue()) {
            throw new DomainException("Khu vực " + area.getName() + " đã đạt số bàn tối đa");

        }
        this.area = area;
        return true;
    }

    public TableNumber getTableNumber() {
        return TableNumber.of(this.tableNumber);
    }

    public Optional<Area> getArea() {
        return Optional.ofNullable(area);
    }

    public boolean setTableNumber(@NotNull TableNumber tableNumber) {
        if (TableNumber.of(this.tableNumber).equals(tableNumber)) {
            return false;
        }

        this.tableNumber = tableNumber.getValue();
        return true;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ServiceTable that = (ServiceTable) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public static class ServiceTableBuilder {
        private @NotNull Integer id;
        private Area area;
        private @NotNull
        @Size(max = 10, message = "Số bàn không được vượt quá 10 ký tự")
        @NotBlank(message = "Số bàn không được để trống") String tableNumber;
        private Boolean active;

        ServiceTableBuilder() {
        }

        public ServiceTableBuilder id(@NotNull Integer id) {
            this.id = id;
            return this;
        }

        public ServiceTableBuilder area(Area area) {
            this.area = area;
            return this;
        }

        public ServiceTableBuilder tableNumber(@NotNull TableNumber tableNumber) {
            this.tableNumber = tableNumber.getValue();
            return this;
        }

        public ServiceTableBuilder active(Boolean active) {
            this.active = active;
            return this;
        }

        public ServiceTable build() {
            return new ServiceTable(this.id, this.area, this.tableNumber, this.active);
        }

        public String toString() {
            return "ServiceTable.ServiceTableBuilder(id=" + this.id + ", area=" + this.area + ", tableNumber=" + this.tableNumber + ", active=" + this.active + ")";
        }
    }
}