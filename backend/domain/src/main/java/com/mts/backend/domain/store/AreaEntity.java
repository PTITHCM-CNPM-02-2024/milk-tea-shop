package com.mts.backend.domain.store;

import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.value_object.AreaName;
import com.mts.backend.domain.store.value_object.MaxTable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.SqlTypes;

import java.util.Objects;
import java.util.Optional;


@Entity
@Table(name = "Area", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "Area_pk", columnNames = {"name"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@Builder
public class AreaEntity extends BaseEntity<AreaId> {
    @Id
    @Comment("Mã khu vực")
    @Column(name = "area_id", nullable = false, columnDefinition = "smallint UNSIGNED")
    @Convert(converter = AreaId.AreaIdConverter.class)
    @NotNull
    @Getter
    @Setter
    private AreaId id;

    @Comment("Tên khu vực")
    @Column(name = "name", nullable = false, length = 3)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Convert(converter = AreaName.AreaNameConverter.class)
    @NotNull
    private AreaName name;

    @Comment("Mô tả")
    @Column(name = "description")
    private String description;

    @Comment("Số bàn tối đa")
    @Column(name = "max_tables")
    @Convert(converter = MaxTable.MaxTableConverter.class)
    private MaxTable maxTable;

    @Getter
    @Comment("Trạng thái hoạt động (1: Có, 0: Không)")
    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    @NotNull
    private Boolean active;

    public AreaEntity(@NotNull AreaId id, @NotNull AreaName name, String description, MaxTable maxTable, @NotNull Boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxTable = maxTable;
        this.active = active;
    }

    public AreaEntity() {
    }
    
    public boolean changeAreaName(AreaName name){
        if (this.name.equals(name)){
            return false;
        }
        
        this.name = name;
        return true;
    }
    
    public boolean changeDescription(String description){
        if (Objects.equals(this.description, description)){
            return false;
        }
        
        this.description = description;
        return true;
    }
    
    public boolean changeMaxTable(MaxTable maxTable){
        if (Objects.equals(this.maxTable, maxTable)){
            return false;
        }
        
        this.maxTable = maxTable;
        return true;
    }
    
    public boolean changeActive(Boolean active){
        if (this.active == active){
            return false;
        }
        
        this.active = active;
        return true;
    }
    
    public AreaName getName() {
        return name;
    }
    
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
    
    public Optional<MaxTable> getMaxTable() {
        return Optional.ofNullable(maxTable);
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        AreaEntity that = (AreaEntity) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
    
    
}