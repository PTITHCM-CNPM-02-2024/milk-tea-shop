package com.mts.backend.domain.store;

import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.store.value_object.AreaName;
import com.mts.backend.domain.store.value_object.MaxTable;
import com.mts.backend.shared.exception.DomainException;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.validator.constraints.Range;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;


@Entity
@Table(name = "area", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "area_pk", columnNames = {"name"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@AllArgsConstructor
@NoArgsConstructor
public class Area extends BaseEntity<Integer> {
    @Id
    @Comment("Mã khu vực")
    @Column(name = "area_id", nullable = false, columnDefinition = "smallint UNSIGNED")
    @NotNull
    @Getter
    private Integer id;

    private static Set<ServiceTable> $default$serviceTables() {
        return new LinkedHashSet<>();
    }

    public static AreaBuilder builder() {
        return new AreaBuilder();
    }

    public boolean setId(@NotNull Integer areaId) {
        if (this.id.equals(areaId)) {
            return false;
        }

        this.id = areaId;
        return true;
    }

    @Comment("Tên khu vực")
    @Column(name = "name", nullable = false, length = 3)
    @NotBlank(message = "Tên khu vực không được để trống")
    @Size(max = 3, min = 3, message = "Tên khu vực phải có đúng 3 ký tự")
    private String name;

    @Comment("Mô tả")
    @Column(name = "description")
    private String description;

    @Comment("Số bàn tối đa")
    @Column(name = "max_tables")
    @Range(min = 0, max = 100, message = "Số bàn tối đa không được vượt quá 100")
    @Nullable
    private Integer maxTable;

    @Getter
    @Comment("Trạng thái hoạt động (1: Có, 0: Không)")
    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    @NotNull
    @Setter
    private Boolean active;

    @Setter
    @OneToMany(mappedBy = "area", fetch = FetchType.LAZY)
    private Set<ServiceTable> serviceTables = new LinkedHashSet<>();

    public Set<ServiceTable> getServiceTables() {
        return Set.copyOf(serviceTables);
    }

    public boolean setName(@NotNull AreaName name) {
        if (AreaName.of(this.name).equals(name)) {
            return false;
        }

        this.name = name.getValue();
        return true;
    }

    public boolean setDescription(@Nullable String description) {
        if (Objects.equals(this.description, description)) {
            return false;
        }

        this.description = description;
        return true;
    }

    public boolean setMaxTable(@Nullable MaxTable maxTable) {
        if (this.getMaxTable().isPresent() && Objects.equals(this.getMaxTable().get(), maxTable)) {
            return false;
        }
        
        validate(maxTable, this.serviceTables);
        this.maxTable = maxTable != null ? maxTable.getValue() : null;
        return true;
    }

    public AreaName getName() {
        return AreaName.of(this.name);
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public Optional<MaxTable> getMaxTable() {
        return Optional.ofNullable(maxTable).map(MaxTable::of);
    }
    
    private void validate(MaxTable maxTable, Set<ServiceTable> serviceTables) {
        if (maxTable == null) {
            return;
        }
        if (maxTable.getValue() < serviceTables.size()) {
            throw new DomainException("Giá trị số bàn tối đa %d không được nhỏ hơn số bàn hiện tại %d".formatted(maxTable.getValue(), serviceTables.size()));
        }
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Area that = (Area) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }


    public static class AreaBuilder {
        private @NotNull Integer id;
        private @NotBlank(message = "Tên khu vực không được để trống")
        @Size(max = 3, min = 3, message = "Tên khu vực phải có đúng 3 ký tự") String name;
        private String description;
        private @Range(min = 0, max = 100, message = "Số bàn tối đa không được vượt quá 100") Integer maxTable;
        private @NotNull Boolean active;
        private Set<ServiceTable> serviceTables$value;
        private boolean serviceTables$set;

        AreaBuilder() {
        }

        public AreaBuilder id(@NotNull Integer id) {
            this.id = id;
            return this;
        }

        public AreaBuilder name(@NotNull AreaName name) {
            this.name = name.getValue();
            return this;
        }

        public AreaBuilder description(String description) {
            this.description = description;
            return this;
        }

        public AreaBuilder maxTable(@Nullable MaxTable maxTable) {
            this.maxTable = maxTable != null ? maxTable.getValue() : null;
            return this;
        }

        public AreaBuilder active(@NotNull Boolean active) {
            this.active = active;
            return this;
        }

        public AreaBuilder serviceTables(Set<ServiceTable> serviceTables) {
            this.serviceTables$value = serviceTables;
            this.serviceTables$set = true;
            return this;
        }

        public Area build() {
            Set<ServiceTable> serviceTables$value = this.serviceTables$value;
            if (!this.serviceTables$set) {
                serviceTables$value = Area.$default$serviceTables();
            }
            return new Area(this.id, this.name, this.description, this.maxTable, this.active, serviceTables$value);
        }

        public String toString() {
            return "Area.AreaBuilder(id=" + this.id + ", name=" + this.name + ", description=" + this.description + ", maxTable=" + this.maxTable + ", active=" + this.active + ", serviceTables$value=" + this.serviceTables$value + ")";
        }
    }
}