package com.mts.backend.domain.product;

import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.product.value_object.UnitName;
import com.mts.backend.domain.product.value_object.UnitSymbol;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.Optional;

@Getter
@Setter
@Entity
@Table(name = "UnitOfMeasure", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "UnitOfMeasure_pk", columnNames = {"name"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitOfMeasureEntity extends BaseEntity <Integer> {
    @Id
    @Comment("Mã đơn vị tính")
    @Column(name = "unit_id", columnDefinition = "smallint UNSIGNED")
    @NotNull
    private Integer id;

    @Comment("Tên đơn vị tính (cái, cc, ml, v.v.)")
    @Column(name = "name", nullable = false, length = 30)
    @NotNull
    @Convert(converter = UnitName.UnitNameConverter.class)
    private UnitName name;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        UnitOfMeasureEntity that = (UnitOfMeasureEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Comment("Ký hiệu (cc, ml, v.v.)")
    @Column(name = "symbol", length = 5, nullable = false)
    @NotNull
    @Convert(converter = UnitSymbol.UnitSymbolConverter.class)
    private UnitSymbol symbol;

    @Comment("Mô tả")
    @Column(name = "description", length = 1000)
    @Nullable
    private String description;
    
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
    
    public boolean changeUnitName(UnitName name){
        if (name.equals(this.name)) {
            return false;
        }
        
        this.name = name;
        return true;
    }
    
    public boolean changeUnitSymbol (UnitSymbol symbol){
        if (symbol.equals(this.symbol)) {
            return false;
        }
        this.symbol = symbol;
        return true;
    }

}