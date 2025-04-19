package com.mts.backend.domain.payment;

import com.mts.backend.domain.payment.identifier.PaymentMethodId;
import com.mts.backend.domain.payment.value_object.PaymentMethodName;
import com.mts.backend.domain.persistence.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.lang.Nullable;

import java.util.Optional;


@Entity
@Table(name = "payment_method", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "payment_method_pk", columnNames = {"payment_name"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethod extends BaseEntity<Integer> {
    @Id
    @Comment("Mã phương thức thanh toán")
    @Column(name = "payment_method_id", columnDefinition = "tinyint UNSIGNED")
    @NotNull
    @Getter
    private Integer id;

    @Comment("Tên phương thức thanh toán")
    @Column(name = "payment_name", nullable = false, length = 50)
    @NotNull
    @Size(max = 50, message = "Tên phương thức thanh toán không được vượt quá 50 ký tự")
    @NotBlank(message = "Tên phương thức thanh toán không được để trống")
    private String name;
    @Comment("Mô tả phương thức thanh toán")
    @Column(name = "payment_description")
    @Nullable
    @Size(max = 1000, message = "Mô tả phương thức thanh toán không được vượt quá 1000 ký tự")
    @Setter
    private String description;

    public static PaymentMethodBuilder builder() {
        return new PaymentMethodBuilder();
    }

    public boolean setId(@NotNull PaymentMethodId id) {
        if (PaymentMethodId.of(this.id).equals(id)) {
            return false;
        }
        this.id = id.getValue();
        return true;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public boolean setName(@NotNull PaymentMethodName name) {
        if (PaymentMethodName.of(this.name).equals(name)) {
            return false;
        }
        this.name = name.getValue();
        return true;
    }

    public PaymentMethodName getName() {
        return PaymentMethodName.of(name);
    }

    public static class PaymentMethodBuilder {
        private @NotNull Integer id;
        private @NotNull
        @Size(max = 50, message = "Tên phương thức thanh toán không được vượt quá 50 ký tự")
        @NotBlank(message = "Tên phương thức thanh toán không được để trống") String name;
        private @Size(max = 1000, message = "Mô tả phương thức thanh toán không được vượt quá 1000 ký tự") String description;

        PaymentMethodBuilder() {
        }

        public PaymentMethodBuilder id(@NotNull Integer id) {
            this.id = id;
            return this;
        }

        public PaymentMethodBuilder name(@NotNull PaymentMethodName name) {
            this.name = name.getValue();
            return this;
        }

        public PaymentMethodBuilder description(@Size(max = 1000, message = "Mô tả phương thức thanh toán không được vượt quá 1000 ký tự") String description) {
            this.description = description;
            return this;
        }

        public PaymentMethod build() {
            return new PaymentMethod(this.id, this.name, this.description);
        }

        public String toString() {
            return "PaymentMethod.PaymentMethodBuilder(id=" + this.id + ", name=" + this.name + ", description=" + this.description + ")";
        }
    }
}