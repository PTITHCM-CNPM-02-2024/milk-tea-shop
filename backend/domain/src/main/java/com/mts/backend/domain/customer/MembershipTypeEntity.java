package com.mts.backend.domain.customer;

import com.mts.backend.domain.common.value_object.MemberDiscountValue;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.value_object.MemberTypeName;
import com.mts.backend.domain.persistence.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Getter
@Setter
@Entity
@Table(name = "MembershipType", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "MembershipType_pk", columnNames = {"type"}),
        @UniqueConstraint(name = "MembershipType_pk_2", columnNames = {"required_point"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@Builder
public class MembershipTypeEntity extends BaseEntity<Integer> {
    @Id
    @Comment("Mã loại thành viên")
    @Column(name = "membership_type_id", columnDefinition = "tinyint UNSIGNED")
    @NotNull
    private Integer id;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        MembershipTypeEntity that = (MembershipTypeEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "discount_value", precision = 10, scale = 3)),
            @AttributeOverride(name = "unit", column = @Column(name = "discount_unit"))
    })
    private MemberDiscountValue memberDiscountValue;

    @Comment("Điểm yêu cầu")
    @Column(name = "required_point", nullable = false)
    @NotNull
    @PositiveOrZero(message = "Số điểm yêu cầu phải lớn hơn hoặc bằng 0")
    private Integer requiredPoint;

    @Comment("Mô tả")
    @Column(name = "description")
    @Nullable
    private String description;

    @Comment("Ngày hết hạn")
    @Column(name = "valid_until")
    @Nullable
    private LocalDateTime validUntil;

    @Comment("Trạng thái (1: Hoạt động, 0: Không hoạt động)")
    @ColumnDefault("1")
    @Column(name = "is_active")
    private Boolean active;

    @Comment("Loại thành viên")
    @Column(name = "type", nullable = false, length = 50)
    @NotNull
    @Convert(converter = MemberTypeName.MemberTypeNameConverter.class)
    private MemberTypeName type;

    public MembershipTypeEntity(Integer id, MemberDiscountValue memberDiscountValue, @NotNull Integer requiredPoint, @Nullable String description, @NotNull LocalDateTime validUntil, Boolean active, @NotNull MemberTypeName type) {
        this.id = id;
        this.memberDiscountValue = memberDiscountValue;
        this.requiredPoint = requiredPoint;
        this.description = description;
        this.validUntil = validUntil;
        this.active = active;
        this.type = type;
    }

    public MembershipTypeEntity() {
    }
    
    public boolean changeDiscountValue(MemberDiscountValue memberDiscountValue) {
        if (memberDiscountValue.equals(this.memberDiscountValue)) {
            return false;
        }
        this.memberDiscountValue = memberDiscountValue;
        return true;
    }
    
    public boolean changeRequiredPoint(int requiredPoint) {
        if (this.requiredPoint == requiredPoint) {
            return false;
        }
        this.requiredPoint = requiredPoint;
        return true;
    }
    
    public boolean changeDescription(String description) {
        if (Objects.equals(this.description, description)) {
            return false;
        }
        this.description = description;
        return true;
    }
    
    public boolean changeValidUntil(LocalDateTime validUntil) {
        if (Objects.equals(this.validUntil, validUntil)) {
            return false;
        }
        this.validUntil = validUntil;
        return true;
    }
    
    public boolean changeType(MemberTypeName type) {
        if (Objects.equals(this.type, type)) {
            return false;
        }
        this.type = type;
        return true;
    }
    
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
    
    public Optional<LocalDateTime> getValidUntil() {
        return Optional.ofNullable(validUntil);
    }
}