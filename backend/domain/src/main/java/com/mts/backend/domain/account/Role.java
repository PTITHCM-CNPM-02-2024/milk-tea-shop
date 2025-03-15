package com.mts.backend.domain.account;

import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.value_object.RoleName;
import com.mts.backend.shared.domain.AbstractAggregateRoot;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Role extends AbstractAggregateRoot<RoleId> {
    
    @NonNull
    private RoleName roleName;
    
    @Nullable
    private String description;
    
    private final LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt;
    
    public Role(@NonNull RoleId roleId, @NonNull RoleName roleName, @Nullable String description, LocalDateTime updatedAt) {
        super(roleId);
        Objects.requireNonNull(roleName, "Role name is required");
        this.roleName = roleName;
        this.description = description;
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }
    
    public RoleName getRoleName() {
        return roleName;
    }
    
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public boolean changeRoleName(RoleName roleName) {
        Objects.requireNonNull(roleName, "Role name is required");
        if (this.roleName.equals(roleName)) {
            return false;
        }
        this.roleName = roleName;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    public boolean changeDescription(String description) {
        if (Objects.equals(this.description, description)) {
            return false;
        }
        this.description = description;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    
}
