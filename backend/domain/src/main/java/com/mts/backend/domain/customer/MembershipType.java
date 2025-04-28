package com.mts.backend.domain.customer;

import com.mts.backend.domain.common.value_object.MemberDiscountValue;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.value_object.MemberTypeName;
import com.mts.backend.domain.persistence.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;


@Entity
@Table(name = "membership_type", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "membership_type_pk", columnNames = {"type"}),
        @UniqueConstraint(name = "membership_type_pk_2", columnNames = {"required_point"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@NoArgsConstructor
@AllArgsConstructor
public class MembershipType extends BaseEntity<Integer> {
    @Id
    @Comment("Mã loại thành viên")
    @Column(name = "membership_type_id", columnDefinition = "tinyint UNSIGNED")
    @NotNull
    @Getter
    private Integer id;

    @OneToMany(mappedBy = "membershipType")
    private Set<Customer> customers = new LinkedHashSet<>();
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "discount_value", precision = 10, scale = 3)),
            @AttributeOverride(name = "unit", column = @Column(name = "discount_unit"))
    })
    @Getter
    private MemberDiscountValue memberDiscountValue;
    @Comment("Điểm yêu cầu")
    @Column(name = "required_point", nullable = false)
    @NotNull
    @PositiveOrZero(message = "Số điểm yêu cầu phải lớn hơn hoặc bằng 0")
    @Getter
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
    @Getter
    @Setter
    private Boolean active;
    @Comment("Loại thành viên")
    @Column(name = "type", nullable = false, length = 50)
    @NotNull
    @Size(max = 50, message = "Tên loại thành viên không được vượt quá 50 ký tự")
    @NotBlank(message = "Tên loại thành viên không được để trống")
    private String type;

    public static MembershipTypeBuilder builder() {
        return new MembershipTypeBuilder();
    }

    private static Set<Customer> $default$customers() {
        return new LinkedHashSet<>();
    }

    public boolean setId(@NotNull MembershipTypeId id) {
        if (MembershipTypeId.of(this.id).equals(id)) {
            return false;
        }
        this.id = id.getValue();
        return true;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        MembershipType that = (MembershipType) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public Set<Customer> getCustomers() {
        return Set.copyOf(customers);
    }

    public void setCustomers(Set<Customer> customers) {
        this.customers = customers;
    }


    public boolean setMemberDiscountValue(@NotNull MemberDiscountValue memberDiscountValue) {
        if (memberDiscountValue.equals(this.memberDiscountValue)) {
            return false;
        }
        this.memberDiscountValue = memberDiscountValue;
        return true;
    }

    public boolean setRequiredPoint(@PositiveOrZero(message = "Số điểm yêu cầu phải lớn hơn hoặc bằng 0") @NotNull int requiredPoint) {
        if (this.requiredPoint == requiredPoint) {
            return false;
        }
        this.requiredPoint = requiredPoint;
        return true;
    }

    public boolean setDescription(String description) {
        if (Objects.equals(this.description, description)) {
            return false;
        }
        this.description = description;
        return true;
    }

    public boolean setValidUntil(@Nullable LocalDateTime validUntil) {
        if (Objects.equals(this.validUntil, validUntil)) {
            return false;
        }
        this.validUntil = validUntil;
        return true;
    }

    public boolean setType(@NotNull MemberTypeName type) {
        if (MemberTypeName.of(this.type).equals(type)) {
            return false;
        }
        this.type = type.getValue();
        return true;
    }

    public MemberTypeName getType() {
        return MemberTypeName.of(this.type);
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public Optional<LocalDateTime> getValidUntil() {
        return Optional.ofNullable(validUntil);
    }

    public static class MembershipTypeBuilder {
        private @NotNull Integer id;
        private MemberDiscountValue memberDiscountValue;
        private @NotNull
        @PositiveOrZero(message = "Số điểm yêu cầu phải lớn hơn hoặc bằng 0") Integer requiredPoint;
        private String description;
        private LocalDateTime validUntil;
        private Boolean active;
        private @NotNull
        @Size(max = 50, message = "Tên loại thành viên không được vượt quá 50 ký tự")
        @NotBlank(message = "Tên loại thành viên không được để trống") String type;
        private boolean customers$set;
        private Set<Customer> customers$value;

        MembershipTypeBuilder() {
        }

        public MembershipTypeBuilder id(@NotNull Integer id) {
            this.id = id;
            return this;
        }

        public MembershipTypeBuilder memberDiscountValue(MemberDiscountValue memberDiscountValue) {
            this.memberDiscountValue = memberDiscountValue;
            return this;
        }

        public MembershipTypeBuilder requiredPoint(@NotNull @PositiveOrZero(message = "Số điểm yêu cầu phải lớn hơn hoặc bằng 0") Integer requiredPoint) {
            this.requiredPoint = requiredPoint;
            return this;
        }

        public MembershipTypeBuilder description(@Nullable String description) {
            this.description = description;
            return this;
        }

        public MembershipTypeBuilder validUntil(@Nullable LocalDateTime validUntil) {
            this.validUntil = validUntil;
            return this;
        }

        public MembershipTypeBuilder active(Boolean active) {
            this.active = active;
            return this;
        }

        public MembershipTypeBuilder type(@NotNull MemberTypeName type) {
            this.type = type.getValue();
            return this;
        }

        public MembershipTypeBuilder customers(@NotNull Set<Customer> customers) {
            this.customers$set = true;
            this.customers$value = customers;
            return this;
        }

        public MembershipType build() {
            Set<Customer> customers$value = this.customers$value;
            if (!this.customers$set) {
                customers$value = MembershipType.$default$customers();
            }
            return new MembershipType(this.id, customers$value, this.memberDiscountValue, this.requiredPoint,
                    this.description,
                    this.validUntil, this.active, this.type);
        }

        public String toString() {
            return "MembershipType.MembershipTypeBuilder(id=" + this.id + ", memberDiscountValue=" + this.memberDiscountValue + ", requiredPoint=" + this.requiredPoint + ", description=" + this.description + ", validUntil=" + this.validUntil + ", active=" + this.active + ", type=" + this.type + ")";
        }
    }
}