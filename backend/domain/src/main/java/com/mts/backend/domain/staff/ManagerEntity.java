package com.mts.backend.domain.staff;

import com.mts.backend.domain.account.AccountEntity;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.persistence.BaseEntity;
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
@Table(name = "manager", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "account_id", columnNames = {"account_id"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@Builder
public class ManagerEntity extends BaseEntity<Long> {
    public ManagerEntity(Long id, AccountEntity accountEntity, @NotNull LastName lastName, @NotNull FirstName firstName, Gender gender, @NotNull PhoneNumber phone, @NotNull Email email) {
        this.id = id;
        this.accountEntity = accountEntity;
        this.lastName = lastName;
        this.firstName = firstName;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
    }

    public ManagerEntity() {
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ManagerEntity that = (ManagerEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Id
    @Comment("Mã quản lý")
    @Column(name = "manager_id", columnDefinition = "int UNSIGNED")
    @NotNull
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @Comment("Mã tài khoản")
    @JoinColumn(name = "account_id")
    private AccountEntity accountEntity;

    @Comment("Họ")
    @Column(name = "last_name", nullable = false, length = 70)
    @NotNull
    @Convert(converter = LastName.LastNameConverter.class)
    private LastName lastName;

    @Comment("Tên")
    @Column(name = "first_name", nullable = false, length = 70)
    @NotNull
    @Convert(converter = com.mts.backend.domain.common.value_object.FirstName.FirstNameConverter.class)
    private FirstName firstName;

    @Comment("Giới tính")
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
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
    
    public boolean changeFirstName(FirstName firstName) {
        Objects.requireNonNull(firstName, "First name is required");
        if (this.firstName.equals(firstName)) {
            return false;
        }
        this.firstName = firstName;
        return true;
    }

    public boolean changeLastName(LastName lastName) {
        Objects.requireNonNull(lastName, "Last name is required");
        if (this.lastName.equals(lastName)) {
            return false;
        }
        this.lastName = lastName;
        return true;
    }

    public boolean changeEmail(Email email) {
        Objects.requireNonNull(email, "Email is required");
        if (this.email.equals(email)) {
            return false;
        }
        this.email = email;
        return true;
    }

    public boolean changePhoneNumber(PhoneNumber phoneNumber) {
        Objects.requireNonNull(phoneNumber, "Phone number is required");
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
    

}