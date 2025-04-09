package com.mts.backend.domain.account;

import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.value_object.RoleName;
import com.mts.backend.domain.persistence.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.util.Objects;
import java.util.Optional;

@Getter
@Setter
@Entity
@Table(name = "role", schema = "milk_tea_shop_prod", uniqueConstraints = {
        @UniqueConstraint(name = "role_pk", columnNames = {"name"})
})
@AttributeOverrides({
        @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
        @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at"))
})
@Builder
public class RoleEntity extends BaseEntity<Integer> {
    @Id
    @Comment("Mã vai trò")
    @Column(name = "role_id", columnDefinition = "tinyint unsigned")
    @NotNull
    private Integer id;

    @Comment("Tên vai trò (ví dụ: admin, staff, customer)")
    @Column(name = "name", nullable = false, length = 50)
    @Convert(converter = RoleName.RoleNameConverter.class)
    @NotNull
    private RoleName name;

    @Comment("Mô tả vai trò")
    @Column(name = "description", length = 1000)
    private String description;

    public RoleEntity(Integer id, @NotNull RoleName name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public RoleEntity() {
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
    
    public boolean changeRoleName(RoleName newRoleName) {
        if (Objects.equals(this.name, newRoleName)) {
            return false;
        }
        
        this.name = newRoleName;
        return true;
    }
    
    public boolean changeDescription(String newDescription) {
        if (Objects.equals(this.description, newDescription)) {
            return false;
        }
        
        this.description = newDescription;
        return true;
    }

}