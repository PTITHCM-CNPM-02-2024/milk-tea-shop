package com.mts.backend.domain.account.repository;

import com.mts.backend.domain.account.Role;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.value_object.RoleName;
import com.mts.backend.domain.account.jpa.JpaRoleRepository;
import com.mts.backend.domain.persistence.entity.RoleEntity;
import com.mts.backend.shared.exception.DomainException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RoleRepository implements IRoleRepository {
    private final JpaRoleRepository jpaRoleRepository;
    
    public RoleRepository(JpaRoleRepository jpaRoleRepository) {
        this.jpaRoleRepository = jpaRoleRepository;
    }
    /**
     * @param roleName 
     * @return
     */
    @Override
    public boolean existsByName(RoleName roleName) {
        Objects.requireNonNull(roleName, "Role name is required");
        return jpaRoleRepository.existsByName(roleName.getValue());
    }

    /**
     * @param roleId 
     * @return
     */
    @Override
    public boolean existsById(RoleId roleId) {
        Objects.requireNonNull(roleId, "Role id is required");
        return jpaRoleRepository.existsById(roleId.getValue());
    }

    /**
     * @param roleId 
     * @return
     */
    @Override
    public Optional<Role> findById(RoleId roleId) {
        Objects.requireNonNull(roleId, "Role id is required");
        
        return jpaRoleRepository.findById(roleId.getValue())
                .map(roleEntity -> new Role(RoleId.of(roleEntity.getId()), RoleName.of(roleEntity.getName()), roleEntity.getDescription(), roleEntity.getUpdatedAt().orElse(LocalDateTime.now())));
        
        
    }

    /**
     * @param role 
     * @return
     */
    @Override
    @Transactional
    public Role save(Role role) {
        Objects.requireNonNull(role, "Role is required");
        try {
            if (!jpaRoleRepository.existsById(role.getId().getValue())) {
                return create(role);
            } else {
                return update(role);
            }
        }catch (Exception e) {
            throw new DomainException("Không thể lưu role", e);
        }
    }
    
    @Transactional
    protected Role create(Role role) {
        try {
            RoleEntity roleEntity = RoleEntity.builder()
                    .id(role.getId().getValue())
                    .name(role.getRoleName().getValue())
                    .description(role.getDescription().orElse(""))
                    .build();
            
            jpaRoleRepository.insertRole(roleEntity);
            return role;
        }catch (Exception e) {
            throw new DomainException("Không thể tạo role", e);
        }
    }
    
    @Transactional
    protected Role update(Role role) {
        try {
            RoleEntity roleEntity = RoleEntity.builder()
                    .id(role.getId().getValue())
                    .name(role.getRoleName().getValue())
                    .description(role.getDescription().orElse(""))
                    .build();
            
            jpaRoleRepository.updateRole(roleEntity);
            return role;
        }catch (Exception e) {
            throw new DomainException("Không thể cập nhật role", e);
        }
    }
    

    /**
     * @return 
     */
    @Override
    public List<Role> findAll() {
        return jpaRoleRepository.findAll().stream()
                .map(roleEntity -> new Role(RoleId.of(roleEntity.getId()), RoleName.of(roleEntity.getName()), roleEntity.getDescription(), roleEntity.getUpdatedAt().orElse(LocalDateTime.now())))
                .toList();
    }
}
