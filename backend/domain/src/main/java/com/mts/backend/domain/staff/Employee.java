package com.mts.backend.domain.staff;

import com.mts.backend.domain.account.Account;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.persistence.BaseEntity;
import com.mts.backend.domain.staff.identifier.EmployeeId;
import com.mts.backend.domain.staff.value_object.Position;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;


@Entity
@Table(name = "employee", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "account_id", columnNames = {"account_id"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends BaseEntity<Long> {
    @Id
    @Comment("Mã nhân viên")
    @Column(name = "employee_id", columnDefinition = "int UNSIGNED")
    @NotNull
    @Getter
    private Long id;
    
    public static EmployeeEntityBuilder builder() {
        return new EmployeeEntityBuilder();
    }

    private boolean setId(@NotNull EmployeeId id) {
        if (EmployeeId.of(this.id).equals(id)) {
            return false;
        }
        this.id = id.getValue();
        return true;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @Comment("Mã tài khoản")
    @JoinColumn(name = "account_id")
    @NotNull
    @Getter
    private Account account;
    

    public boolean setAccount(@NotNull Account account) {
        if (Objects.equals(this.account, account)) {
            return false;
        }
        this.account = account;
        return true;
    }


    @Comment("Chức vụ")
    @Column(name = "position", nullable = false, length = 50)
    @NotBlank(message = "Chức vụ không được để trống")
    @Size(max = 50, message = "Chức vụ không được vượt quá 50 ký tự")
    private String position;

    public Position getPosition() {
        return Position.of(this.position);
    }

    @Comment("Họ")
    @Column(name = "last_name", nullable = false, length = 70)
    @NotBlank(message = "Họ không được để trống")
    @Size(max = 70, message = "Họ không được vượt quá 70 ký tự")
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
    @NotNull
    @Getter
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
        if (this.gender.equals(gender)) {
            return false;
        }

        this.gender = gender;
        return true;
    }

    public boolean setPosition(@NotNull Position position) {
        if (Position.of(this.position).equals(position)) {
            return false;
        }
        this.position = position.getValue();
        return true;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Employee employee = (Employee) o;
        return getId() != null && Objects.equals(getId(), employee.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public static class EmployeeEntityBuilder {
        private @NotNull Long id;
        private @NotNull Account account;
        private @NotBlank(message = "Chức vụ không được để trống")
        @Size(max = 50, message = "Chức vụ không được vượt quá 50 ký tự") String position;
        private @NotBlank(message = "Họ không được để trống")
        @Size(max = 70, message = "Họ không được vượt quá 70 ký tự") String lastName;
        private @NotNull
        @Size(max = 70, message = "Tên không được vượt quá 70 ký tự")
        @NotBlank(message = "Tên không được để trống") String firstName;
        private @NotNull Gender gender;
        private @NotNull
        @Size(max = 15, message = "Số điện thoại không được vượt quá 15 ký tự")
        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "(?:\\+84|0084|0)[235789][0-9]{1,2}[0-9]{7}(?:[^\\d]+|$)", message = "Số điện thoại không hợp lệ") String phone;
        private @NotNull
        @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
        @NotBlank(message = "Email không được để trống")
        @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Email không hợp lệ") String email;

        EmployeeEntityBuilder() {
        }

        public EmployeeEntityBuilder id(@NotNull Long id) {
            this.id = id;
            return this;
        }

        public EmployeeEntityBuilder accountEntity(@NotNull Account account) {
            this.account = account;
            return this;
        }

        public EmployeeEntityBuilder position(@NotNull Position position) {
            this.position = position.getValue();
            return this;
        }

        public EmployeeEntityBuilder lastName(@NotNull LastName lastName) {
            this.lastName = lastName.getValue();
            return this;
        }

        public EmployeeEntityBuilder firstName(@NotNull FirstName firstName) {
            this.firstName = firstName.getValue();
            return this;
        }

        public EmployeeEntityBuilder gender(@NotNull Gender gender) {
            this.gender = gender;
            return this;
        }

        public EmployeeEntityBuilder phone(@NotNull PhoneNumber phone) {
            this.phone = phone.getValue();
            return this;
        }

        public EmployeeEntityBuilder email(@NotNull Email email) {
            this.email = email.getValue();
            return this;
        }

        public Employee build() {
            return new Employee(this.id, this.account, this.position, this.lastName, this.firstName, this.gender, this.phone, this.email);
        }

        public String toString() {
            return "Employee.EmployeeEntityBuilder(id=" + this.id + ", account=" + this.account + ", position=" + this.position + ", lastName=" + this.lastName + ", firstName=" + this.firstName + ", gender=" + this.gender + ", phone=" + this.phone + ", email=" + this.email + ")";
        }
    }
}