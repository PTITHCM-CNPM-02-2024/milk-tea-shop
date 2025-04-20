package com.mts.backend.domain.account;

import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.value_object.RoleName;
import com.mts.backend.domain.persistence.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "role", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "role_pk", columnNames = {"name"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
public class Role extends BaseEntity<Integer> {
    @Id
    @Comment("Mã vai trò")
    @Column(name = "role_id", columnDefinition = "tinyint unsigned")
    @NotNull
    @Getter
    private Integer id;

    public boolean setId(@NotNull RoleId id) {
        if(RoleId.of(this.id).equals(id)){
            return false;
        }
        this.id = id.getValue();
        return true;
    }

    @Comment("Tên vai trò (ví dụ: admin, staff, customer)")
    @Column(name = "name", nullable = false, length = 50)
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$",
            message = "Tên vai trò không hợp lệ")
    @NotBlank(message = "Tên vai trò không được để trống")
    private String name;
    
    public RoleName getName() {
        return RoleName.of(name);
    }

    @Comment("Mô tả vai trò")
    @Column(name = "description", length = 1000)
    private String description;

    public static RoleBuilder builder() {
        return new RoleBuilder();
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public boolean setName(@NotNull RoleName newRoleName) {
        if (RoleName.of(this.name).equals(newRoleName)) {
            return false;
        }
        this.name = newRoleName.getValue();
        return true;
    }

    public boolean setDescription(String newDescription) {
        if (Objects.equals(this.description, newDescription)) {
            return false;
        }

        this.description = newDescription;
        return true;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Role role = (Role) o;
        return getId() != null && Objects.equals(getId(), role.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public Role() {
    }

    public Role(@NotNull Integer id,
                @NotNull @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "Tên vai trò không hợp lệ") @NotBlank(message = "Tên vai trò không được để trống") String name,
                String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static class RoleBuilder {
        private @NotNull Integer id;
        private @NotNull
        @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$",
                message = "Tên vai trò không hợp lệ")
        @NotBlank(message = "Tên vai trò không được để trống") String name;
        private String description;

        RoleBuilder() {
        }

        public RoleBuilder id(@NotNull RoleId id) {
            this.id = id.getValue();
            return this;
        }

        public RoleBuilder name(@NotNull RoleName name) {
            this.name = name.getValue();
            return this;
        }

        public RoleBuilder description(String description) {
            this.description = description;
            return this;
        }

        public Role build() {
            return new Role(this.id, this.name, this.description);
        }

        public String toString() {
            return "Role.RoleBuilder(id=" + this.id + ", name=" + this.name + ", description=" + this.description + ")";
        }
    }
}