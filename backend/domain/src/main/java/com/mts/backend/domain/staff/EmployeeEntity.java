package com.mts.backend.domain.staff;

import com.mts.backend.domain.account.AccountEntity;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.staff.value_object.Position;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "employee", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "account_id", columnNames = {"account_id"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@Builder
public class EmployeeEntity extends BaseEntity<Long> {
    @Id
    @Comment("Mã nhân viên")
    @Column(name = "employee_id", columnDefinition = "int UNSIGNED")
    @NotNull
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @Comment("Mã tài khoản")
    @JoinColumn(name = "account_id")
    @NotNull
    private AccountEntity accountEntity;

    @Comment("Chức vụ")
    @Column(name = "position", nullable = false, length = 50)
    @Convert(converter = Position.PositionConverter.class)
    private Position position;

    @Comment("Họ")
    @Column(name = "last_name", nullable = false, length = 70)
    @Convert(converter = LastName.LastNameConverter.class)
    @NotNull
    private LastName lastName;

    @Comment("Tên")
    @Column(name = "first_name", nullable = false, length = 70)
    @NotNull
    @Convert(converter = FirstName.FirstNameConverter.class)
    private FirstName firstName;

    @Comment("Giới tính")
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Gender gender;

    @Comment("Số điện thoại")
    @Column(name = "phone", nullable = false, length = 15)
    @NotNull
    @Convert(converter = PhoneNumber.PhoneNumberConverter.class)
    private PhoneNumber phone;

    @Comment("Email")
    @Column(name = "email", nullable = false, length = 100)
    @NotNull
    @Convert(converter = Email.EmailConverter.class)
    private Email email;

    public EmployeeEntity(Long id, AccountEntity accountEntity, Position position, @NotNull LastName lastName, @NotNull FirstName firstName, Gender gender, @NotNull PhoneNumber phone, @NotNull Email email) {
        this.id = id;
        this.accountEntity = accountEntity;
        this.position = position;
        this.lastName = lastName;
        this.firstName = firstName;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
    }

    public EmployeeEntity() {
    }

    public boolean changeFirstName(FirstName firstName) {
        if (this.firstName.equals(firstName)) {
            return false;
        }
        this.firstName = firstName;
        return true;
    }

    public boolean changeLastName(LastName lastName) {
        if (this.lastName.equals(lastName)) {
            return false;
        }
        this.lastName = lastName;
        return true;
    }

    public boolean changeEmail(Email email) {
        if (this.email.equals(email)) {
            return false;
        }
        this.email = email;
        return true;
    }

    public boolean changePhoneNumber(PhoneNumber phoneNumber) {
        if (this.phone.equals(phoneNumber)) {
            return false;
        }
        this.phone = phoneNumber;
        return true;
    }

    public boolean changeGender(Gender gender) {
        Objects.requireNonNull(gender, "gender is required");
        if (this.gender.equals(gender)) {
            return false;
        }

        this.gender = gender;
        return true;
    }

    public boolean changePosition(Position position) {
        Objects.requireNonNull(position, "Position is required");
        if (this.position.equals(position)) {
            return false;
        }
        this.position = position;
        return true;
    }
    
    public String getFullName() {
        return this.firstName.getValue() + " " + this.lastName.getValue();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        EmployeeEntity employee = (EmployeeEntity) o;
        return getId() != null && Objects.equals(getId(), employee.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}