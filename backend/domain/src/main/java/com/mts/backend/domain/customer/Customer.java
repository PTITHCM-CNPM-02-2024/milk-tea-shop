package com.mts.backend.domain.customer;

import com.mts.backend.domain.account.Account;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.customer.value_object.RewardPoint;
import com.mts.backend.domain.persistence.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.Optional;

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

@NamedEntityGraphs({
        @NamedEntityGraph(name = "graph.customer.fetchMembershipType",
                attributeNodes = @NamedAttributeNode("membershipType")),
        @NamedEntityGraph(name = "graph.customer.fetchAccount",
                attributeNodes = @NamedAttributeNode("account")),
        @NamedEntityGraph(name = "graph.customer.fetchMembershipTypeAndAccount",
                attributeNodes = {
                        @NamedAttributeNode("membershipType"),
                        @NamedAttributeNode("account")
                })
})
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends BaseEntity<Long> {
    @Id
    @Comment("Mã khách hàng")
    @Column(name = "customer_id", columnDefinition = "int UNSIGNED")
    @NotNull
    @Getter
    private Long id;
    
    public static CustomerBuilder builder() {
        return new CustomerBuilder();
    }

    public boolean setId(@NotNull CustomerId customerId) {
        if (CustomerId.of(this.id).equals(customerId)) {
            return false;
        }
        this.id = customerId.getValue();
        return true;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Customer that = (Customer) o;
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
    @Getter
    private MembershipType membershipType;

    public boolean setMembershipType(@NotNull MembershipType membershipType) {
        if (Objects.equals(this.membershipType, membershipType)) {
            return false;
        }
        this.membershipType = membershipType;
        return true;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("Mã tài khoản")
    @JoinColumn(name = "account_id")
    @Nullable
    private Account account;

    @Comment("Họ")
    @Column(name = "last_name", length = 70)
    @Nullable
    @jakarta.validation.constraints.Size(max = 70, message = "Họ không được vượt quá 70 ký tự")
    private String lastName;

    @Comment("Tên")
    @Column(name = "first_name", length = 70)
    @Nullable
    @jakarta.validation.constraints.Size(max = 70, message = "Họ không được vượt quá 70 ký tự")
    private String firstName;

    @Comment("Số điện thoại")
    @Column(name = "phone", nullable = false, length = 15)
    @NotNull
    @Size(max = 15, message = "Số điện thoại không được vượt quá 15 ký tự")
    @jakarta.validation.constraints.NotBlank(message = "Số điện thoại không được để trống")
    @jakarta.validation.constraints.Pattern(regexp = "(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)", message = "Số điện thoại không hợp lệ")
    private String phone;

    public PhoneNumber getPhone() {
        return PhoneNumber.of(this.phone);
    }

    @Comment("Email")
    @Column(name = "email", length = 100)
    @Nullable
    @Size(max = 100, message = "Email không được vượt quá 255 ký tự")
    @jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Email không hợp lệ")
    private String email;

    @Comment("Điểm hiện tại")
    @ColumnDefault("0")
    @Column(name = "current_points", nullable = false)
    @NotNull
    @PositiveOrZero(message = "Điểm hiện tại không được âm")
    private Integer currentPoint;
    
    public RewardPoint getCurrentPoint() {
        return RewardPoint.of(this.currentPoint);
    }

    @Comment("Giới tính")
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    @Nullable
    private Gender gender;

    public Optional<Gender> getGender() {
        return Optional.ofNullable(gender);
    }


    public boolean setFirstName(@jakarta.annotation.Nullable FirstName firstName) {
        if (getFirstName().isPresent() && Objects.equals(FirstName.of(this.firstName), firstName)) {
            return false;
        }

        this.firstName = firstName == null ? null : firstName.getValue();
        return true;
    }

    public boolean setLastName(@Nullable LastName lastName) {
        if ( getLastName().isPresent() && Objects.equals(LastName.of(this.lastName), lastName)) {
            return false;
        }
        this.lastName = lastName == null ? null : lastName.getValue();
        return true;
    }

    public boolean setPhone(@NotNull PhoneNumber phone) {
        if (PhoneNumber.of(this.phone).equals(phone)) {
            return false;
        }
        this.phone = phone.getValue();
        return true;
    }

    public boolean changeEmail(@Nullable Email email) {
        if (Objects.equals(Email.of(this.email), email)) {
            return false;
        }
        this.email = email == null ? null : email.getValue();
        return true;
    }

    public boolean setGender(@Nullable Gender gender) {
        if (Objects.equals(this.gender, gender)) {
            return false;
        }
        this.gender = gender;
        return true;
    }

    public boolean setCurrentPoint(@NotNull RewardPoint rewardPoint) {
        if (RewardPoint.of(this.currentPoint).equals(rewardPoint)) {
            return false;
        }
        this.currentPoint = rewardPoint.getValue();
        return true;
    }

    public void increaseRewardPoint(@NotNull RewardPoint rewardPoint) {
        this.currentPoint = RewardPoint.of(this.currentPoint).add(rewardPoint).getValue();
    }


    public Optional<Account> getAccount() {
        return Optional.ofNullable(account);
    }

    public Optional<LastName> getLastName() {
        return Optional.ofNullable(lastName).map(LastName::of);
    }

    public Optional<FirstName> getFirstName() {
        return Optional.ofNullable(firstName).map(FirstName::of);
    }

    public Optional<Email> getEmail() {
        return Optional.ofNullable(email).map(Email::of);
    }

    @Transient
    public Optional<String> getFullName() {

        if (this.getFirstName().isEmpty() && this.getLastName().isEmpty()) {
            return Optional.empty();
        }

        StringBuilder fullName = new StringBuilder();

        if (this.getFirstName().isPresent()) {
            fullName.append(this.getFirstName().get().getValue());
        }

        if (this.getLastName().isPresent()) {
            if (!fullName.isEmpty()) {
                fullName.append(" ");
            }
            fullName.append(this.getLastName().get().getValue());
        }
        return Optional.of(fullName.toString());
    }


    public static class CustomerBuilder {
        private @NotNull Long id;
        private @NotNull MembershipType membershipType;
        private Account account;
        private @Size(max = 70, message = "Họ không được vượt quá 70 ký tự")
        @NotBlank(message = "Họ không được để trống") String lastName;
        private @Size(max = 70, message = "Họ không được vượt quá 70 ký tự")
        @NotBlank(message = "Họ không được để trống") String firstName;
        private @NotNull
        @Size(max = 15, message = "Số điện thoại không được vượt quá 15 ký tự")
        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)", message = "Số điện thoại không hợp lệ") String phone;
        private @Size(max = 100, message = "Email không được vượt quá 255 ký tự")
        @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Email không hợp lệ") String email;
        private @NotNull
        @PositiveOrZero(message = "Điểm hiện tại không được âm") Integer currentPoint;
        private Gender gender;

        CustomerBuilder() {
        }

        public CustomerBuilder id(@NotNull Long id) {
            this.id = id;
            return this;
        }

        public CustomerBuilder membershipType(@NotNull MembershipType membershipType) {
            this.membershipType = membershipType;
            return this;
        }

        public CustomerBuilder account(Account account) {
            this.account = account;
            return this;
        }

        public CustomerBuilder lastName(@Nullable LastName lastName) {
            this.lastName = lastName == null ? null : lastName.getValue();
            return this;
        }

        public CustomerBuilder firstName(@Nullable FirstName firstName) {
            this.firstName = firstName == null ? null : firstName.getValue();
            return this;
        }

        public CustomerBuilder phone(@NotNull PhoneNumber phone) {
            this.phone = phone.getValue();
            return this;
        }

        public CustomerBuilder email(@Nullable Email email) {
            this.email = email == null ? null : email.getValue();
            return this;
        }

        public CustomerBuilder currentPoint(@NotNull RewardPoint currentPoint) {
            this.currentPoint = currentPoint.getValue();
            return this;
        }

        public CustomerBuilder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public Customer build() {
            return new Customer(this.id, this.membershipType, this.account, this.lastName, this.firstName, this.phone, this.email, this.currentPoint, this.gender);
        }

        public String toString() {
            return "Customer.CustomerBuilder(id=" + this.id + ", membershipType=" + this.membershipType + ", account=" + this.account + ", lastName=" + this.lastName + ", firstName=" + this.firstName + ", phone=" + this.phone + ", email=" + this.email + ", currentPoint=" + this.currentPoint + ", gender=" + this.gender + ")";
        }
    }
}