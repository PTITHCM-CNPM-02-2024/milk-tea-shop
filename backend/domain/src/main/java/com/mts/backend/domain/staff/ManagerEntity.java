package com.mts.backend.domain.staff;

import com.mts.backend.domain.account.Account;
import com.mts.backend.domain.common.provider.IdentifiableProvider;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.staff.identifier.ManagerId;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.validator.constraints.Range;

import java.util.Objects;


@Entity
@Table(name = "manager", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "account_id", columnNames = {"account_id"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class ManagerEntity extends BaseEntity<Long> {


    public ManagerEntity(@NotNull @Range(min = 1, max = IdentifiableProvider.INT_UNSIGNED_MAX) Long id, @NotNull Account account, @NotNull @Size(max = 70, message = "Họ không được vượt quá 70 ký tự") @NotBlank(message = "Họ không được để trống") String lastName, @NotNull @Size(max = 70, message = "Tên không được vượt quá 70 ký tự") @NotBlank(message = "Tên không được để trống") String firstName, Gender gender, @NotNull @Size(max = 15, message = "Số điện thoại không được vượt quá 15 ký tự") @NotBlank(message = "Số điện thoại không được để trống") @Pattern(regexp = "(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)", message = "Số điện thoại không hợp lệ") String phone, @NotNull @Size(max = 100, message = "Email không được vượt quá 100 ký tự") @NotBlank(message = "Email không được để trống") @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Email không hợp lệ") String email) {
        this.id = id;
        this.account = account;
        this.lastName = lastName;
        this.firstName = firstName;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
    }

    public ManagerEntity() {
    }

    public static ManagerEntityBuilder builder() {
        return new ManagerEntityBuilder();
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
    @Range(min = 1, max = IdentifiableProvider.INT_UNSIGNED_MAX)
    private Long id;

    public boolean setId(@NotNull ManagerId id) {
        if (ManagerId.of(this.id).equals(id)) {
            return false;
        }
        this.id = id.getValue();
        return true;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @Comment("Mã tài khoản")
    @JoinColumn(name = "account_id")
    @NotNull
    private Account account;

    @Comment("Họ")
    @Column(name = "last_name", nullable = false, length = 70)
    @NotNull
    @Size(max = 70, message = "Họ không được vượt quá 70 ký tự")
    @NotBlank(message = "Họ không được để trống")
    private String lastName;

    @Comment("Tên")
    @Column(name = "first_name", nullable = false, length = 70)
    @NotNull
    @Size(max = 70, message = "Tên không được vượt quá 70 ký tự")
    @NotBlank(message = "Tên không được để trống")
    private String firstName;

    @Comment("Giới tính")
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Comment("Số điện thoại")
    @Column(name = "phone", nullable = false, length = 15)
    @NotNull
    @Size(max = 15, message = "Số điện thoại không được vượt quá 15 ký tự")
    @NotBlank(message = "Số điện thoại không được để trống")
    @jakarta.validation.constraints.Pattern(regexp = "(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)", message = "Số điện thoại không hợp lệ")
    private String phone;

    @Comment("Email")
    @Column(name = "email", nullable = false, length = 100)
    @NotNull
    @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
    @NotBlank(message = "Email không được để trống")
    @jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Email không hợp lệ")
    private String email;

    public boolean setFirstName(@NotNull FirstName firstName) {
        if (FirstName.of(this.firstName).equals(firstName)) {
            return false;
        }
        this.firstName = firstName.getValue();
        return true;
    }

    public FirstName getFirstName() {
        return FirstName.of(this.firstName);
    }

    public boolean setLastName(@NotNull LastName lastName) {
        Objects.requireNonNull(lastName, "Last name is required");
        if (LastName.of(this.lastName).equals(lastName)) {
            return false;
        }
        this.lastName = lastName.getValue();
        return true;
    }

    public LastName getLastName() {
        return LastName.of(this.lastName);
    }

    public boolean setEmail(@NotNull Email email) {
        if (Email.of(this.email).equals(email)) {
            return false;
        }
        this.email = email.getValue();
        return true;
    }

    public Email getEmail() {
        return Email.of(this.email);
    }

    public boolean setPhone(@NotNull PhoneNumber phoneNumber) {
        if (PhoneNumber.of(this.phone).equals(phoneNumber)) {
            return false;
        }
        this.phone = phoneNumber.getValue();
        return true;
    }

    public PhoneNumber getPhone() {
        return PhoneNumber.of(this.phone);
    }

    public boolean setGender(@NotNull Gender gender) {
        Objects.requireNonNull(gender, "gender is required");
        if (this.gender.equals(gender)) {
            return false;
        }

        this.gender = gender;
        return true;
    }


    public @NotNull @Range(min = 1, max = IdentifiableProvider.INT_UNSIGNED_MAX) Long getId() {
        return this.id;
    }

    public Gender getGender() {
        return this.gender;
    }

    public static class ManagerEntityBuilder {
        private @NotNull
        @Range(min = 1, max = IdentifiableProvider.INT_UNSIGNED_MAX) Long id;
        private @NotNull Account account;
        private @NotNull
        @Size(max = 70, message = "Họ không được vượt quá 70 ký tự")
        @NotBlank(message = "Họ không được để trống") String lastName;
        private @NotNull
        @Size(max = 70, message = "Tên không được vượt quá 70 ký tự")
        @NotBlank(message = "Tên không được để trống") String firstName;
        private Gender gender;
        private @NotNull
        @Size(max = 15, message = "Số điện thoại không được vượt quá 15 ký tự")
        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)", message = "Số điện thoại không hợp lệ") String phone;
        private @NotNull
        @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
        @NotBlank(message = "Email không được để trống")
        @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Email không hợp lệ") String email;

        ManagerEntityBuilder() {
        }

        public ManagerEntityBuilder id(@NotNull @Range(min = 1, max = IdentifiableProvider.INT_UNSIGNED_MAX) Long id) {
            this.id = id;
            return this;
        }

        public ManagerEntityBuilder accountEntity(@NotNull Account account) {
            this.account = account;
            return this;
        }

        public ManagerEntityBuilder lastName(@NotNull @Size(max = 70, message = "Họ không được vượt quá 70 ký tự") @NotBlank(message = "Họ không được để trống") String lastName) {
            this.lastName = lastName;
            return this;
        }

        public ManagerEntityBuilder firstName(@NotNull FirstName firstName) {
            this.firstName = firstName.getValue();
            return this;
        }

        public ManagerEntityBuilder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public ManagerEntityBuilder phone(@NotNull PhoneNumber phone) {
            this.phone = phone.getValue();
            return this;
        }

        public ManagerEntityBuilder email(Email email) {
            this.email = email.getValue();
            return this;
        }

        public ManagerEntity build() {
            return new ManagerEntity(this.id, this.account, this.lastName, this.firstName, this.gender, this.phone, this.email);
        }

        public String toString() {
            return "ManagerEntity.ManagerEntityBuilder(id=" + this.id + ", account=" + this.account + ", lastName=" + this.lastName + ", firstName=" + this.firstName + ", gender=" + this.gender + ", phone=" + this.phone + ", email=" + this.email + ")";
        }
    }
}