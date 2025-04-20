package com.mts.backend.domain.product;

import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.product.identifier.UnitOfMeasureId;
import com.mts.backend.domain.product.value_object.UnitName;
import com.mts.backend.domain.product.value_object.UnitSymbol;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "unit_of_measure", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "unit_of_measure_pk", columnNames = {"name"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@NoArgsConstructor
@AllArgsConstructor
public class UnitOfMeasure extends BaseEntity<Integer> {
    public static UnitOfMeasureBuilder builder() {
        return new UnitOfMeasureBuilder();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        UnitOfMeasure that = (UnitOfMeasure) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Id
    @Comment("Mã đơn vị tính")
    @Column(name = "unit_id", columnDefinition = "smallint UNSIGNED")
    @NotNull
    @Getter
    private Integer id;

    private boolean setId(@NotNull UnitOfMeasureId id) {
        if (UnitOfMeasureId.of(this.id).equals(id)) {
            return false;
        }
        this.id = id.getValue();
        return true;
    }

    @Comment("Tên đơn vị tính (cái, cc, ml, v.v.)")
    @Column(name = "name", nullable = false, length = 30)
    @NotNull
    @NotBlank(message = "Tên đơn vị đo không được để trống")
    @Size(max = 30, message = "Tên đơn vị đo không được vượt quá 30 ký tự")
    private String name;

    public UnitName getName() {
        return UnitName.of(name);
    }

    public UnitSymbol getSymbol() {
        return UnitSymbol.of(symbol);
    }


    @Comment("Ký hiệu (cc, ml, v.v.)")
    @Column(name = "symbol", length = 5, nullable = false)
    @NotNull
    @NotBlank(message = "Ký hiệu không được để trống")
    @Size(max = 5, message = "Ký hiệu không được vượt quá 5 ký tự")
    private String symbol;

    @Comment("Mô tả")
    @Column(name = "description", length = 1000)
    @Nullable
    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    @Setter
    private String description;

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public boolean setName(@NotNull UnitName name) {
        if (UnitName.of(this.name).equals(name)) {
            return false;
        }

        this.name = name.getValue();
        return true;
    }

    public boolean setSymbol(@NotNull UnitSymbol symbol) {
        if (UnitSymbol.of(this.symbol).equals(symbol)) {
            return false;
        }

        this.symbol = symbol.getValue();
        return true;
    }

    public static class UnitOfMeasureBuilder {
        private @NotNull Integer id;
        private @NotNull
        @NotBlank(message = "Tên đơn vị đo không được để trống")
        @Size(max = 30, message = "Tên đơn vị đo không được vượt quá 30 ký tự") String name;
        private @NotNull
        @NotBlank(message = "Ký hiệu không được để trống")
        @Size(max = 5, message = "Ký hiệu không được vượt quá 5 ký tự") String symbol;
        private String description;

        UnitOfMeasureBuilder() {
        }

        public UnitOfMeasureBuilder id(@NotNull Integer id) {
            this.id = id;
            return this;
        }

        public UnitOfMeasureBuilder name(@NotNull UnitName name) {
            this.name = name.getValue();
            return this;
        }

        public UnitOfMeasureBuilder symbol(@NotNull UnitSymbol symbol) {
            this.symbol = symbol.getValue();
            return this;
        }

        public UnitOfMeasureBuilder description(String description) {
            this.description = description;
            return this;
        }

        public UnitOfMeasure build() {
            return new UnitOfMeasure(this.id, this.name, this.symbol, this.description);
        }

        public String toString() {
            return "UnitOfMeasure.UnitOfMeasureBuilder(id=" + this.id + ", name=" + this.name + ", symbol=" + this.symbol + ", description=" + this.description + ")";
        }
    }
}