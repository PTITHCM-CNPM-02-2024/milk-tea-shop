package com.mts.backend.domain.account.repository;

import com.mts.backend.domain.account.Role;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.value_object.RoleName;

import java.util.List;
import java.util.Optional;

public interface IRoleRepository {
    boolean existsByName(RoleName roleName);
    boolean existsById(RoleId roleId);
    Optional<Role> findById(RoleId roleId);
    Role save(Role role);
    List<Role> findAll();
}
