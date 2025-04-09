package com.mts.backend.domain.customer;

import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.customer.value_object.RewardPoint;
import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.account.AccountEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.Optional;

@Getter
@Setter
@Entity
@Table(name = "customer", schema = "milk_tea_shop_prod", indexes = {
        @Index(name = "membership_type_id", columnList = "membership_type_id"),
        @Index(name = "account_id", columnList = "account_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "phone", columnNames = {"phone"}),
        @UniqueConstraint(name = "email", columnNames = {"email"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@Builder
public class CustomerEntity extends BaseEntity<Long> {
    @Id
    @Comment("Mã khách hàng")
    @Column(name = "customer_id", columnDefinition = "int UNSIGNED")
    @NotNull
    private Long id;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        CustomerEntity that = (CustomerEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã loại thành viên")
    @JoinColumn(name = "membership_type_id")
    @NotNull
    private MembershipTypeEntity membershipTypeEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã tài khoản")
    @JoinColumn(name = "account_id")
    @Nullable
    private AccountEntity accountEntity;

    @Comment("Họ")
    @Column(name = "last_name", length = 70)
    @Nullable
    @Convert(converter = LastName.LastNameConverter.class)
    private LastName lastName;

    @Comment("Tên")
    @Column(name = "first_name", length = 70)
    @Nullable
    @Convert(converter = FirstName.FirstNameConverter.class)
    private FirstName firstName;

    @Getter
    @Comment("Số điện thoại")
    @Column(name = "phone", nullable = false, length = 15)
    @NotNull
    @Convert(converter = PhoneNumber.PhoneNumberConverter.class)
    private PhoneNumber phone;

    @Comment("Email")
    @Column(name = "email", length = 100)
    @Nullable
    @Convert(converter = Email.EmailConverter.class)
    private Email email;

    @Comment("Điểm hiện tại")
    @ColumnDefault("0")
    @Column(name = "current_points", nullable = false)
    @NotNull
    @Convert(converter = RewardPoint.RewardPointConverter.class)
    private RewardPoint currentPoints;

    @Comment("Giới tính")
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    public CustomerEntity(Long id, @NotNull MembershipTypeEntity membershipTypeEntity, @Nullable AccountEntity accountEntity, @Nullable LastName lastName, @Nullable FirstName firstName, @NotNull PhoneNumber phone, @Nullable Email email, @NotNull RewardPoint currentPoints, Gender gender) {
        this.id = id;
        this.membershipTypeEntity = membershipTypeEntity;
        this.accountEntity = accountEntity;
        this.lastName = lastName;
        this.firstName = firstName;
        this.phone = phone;
        this.email = email;
        this.currentPoints = currentPoints;
        this.gender = gender;
    }

    public CustomerEntity() {
    }

    public boolean changeFirstName(FirstName firstName) {
        if (Objects.equals(this.firstName, firstName)) {
            return false;
        }

        this.firstName = firstName;
        return true;
    }

    public boolean changeLastName(LastName lastName) {
        if (Objects.equals(this.lastName, lastName)) {
            return false;
        }
        this.lastName = lastName;
        return true;
    }

    public boolean changePhone(PhoneNumber phone) {
        if (this.phone.equals(phone)) {
            return false;
        }
        this.phone = phone;
        return true;
    }

    public boolean changeEmail(Email email) {
        if (Objects.equals(this.email, email)) {
            return false;
        }
        this.email = email;
        return true;
    }

    public boolean changeGender(Gender gender) {
        if (Objects.equals(this.gender, gender)) {
            return false;
        }
        this.gender = gender;
        return true;
    }

    public boolean changeRewardPoint(RewardPoint rewardPoint) {
        Objects.requireNonNull(rewardPoint, "Reward point is required");
        if (this.currentPoints.equals(rewardPoint)) {
            return false;
        }
        this.currentPoints = rewardPoint;
        return true;
    }

    public boolean addRewardPoint(RewardPoint rewardPoint) {
        Objects.requireNonNull(rewardPoint, "Reward point is required");
        this.currentPoints = this.currentPoints.add(rewardPoint);
        return true;
    }

    public boolean subtractRewardPoint(RewardPoint rewardPoint) {
        Objects.requireNonNull(rewardPoint, "Reward point is required");
        this.currentPoints = this.currentPoints.subtract(rewardPoint);
        return true;
    }

    public boolean increaseRewardPoint() {
        this.currentPoints = this.currentPoints.add(RewardPoint.builder().value(1).build());
        return true;
    }
    
    public boolean increaseRewardPoint(RewardPoint rewardPoint) {
        this.currentPoints = this.currentPoints.add(rewardPoint);
        return true;
    }

    public boolean decreaseRewardPoint() {
        this.currentPoints = this.currentPoints.subtract(RewardPoint.builder().value(1).build());
        return true;
    }
    
    public boolean decreaseRewardPoint(RewardPoint rewardPoint) {
        this.currentPoints = this.currentPoints.subtract(rewardPoint);
        return true;
    }

    public Optional<AccountEntity> getAccountEntity() {
        return Optional.ofNullable(accountEntity);
    }
    
    public Optional<LastName> getLastName() {
        return Optional.ofNullable(lastName);
    }
    
    public Optional<FirstName> getFirstName() {
        return Optional.ofNullable(firstName);
    }
    
    public Optional<Email> getEmail() {
        return Optional.ofNullable(email);
    }
    @Transient
    public Optional<String> getFullName() {
        if (firstName == null && lastName == null) {
            return Optional.empty();
        }
        StringBuilder fullName = new StringBuilder();
        if (firstName != null) {
            fullName.append(firstName.getValue());
        }
        if (lastName != null) {
            fullName.append(" ").append(lastName.getValue());
        }
        return Optional.of(fullName.toString());
    }


}