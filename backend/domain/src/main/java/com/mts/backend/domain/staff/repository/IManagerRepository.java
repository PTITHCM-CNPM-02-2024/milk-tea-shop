package com.mts.backend.domain.staff.repository;

import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.PhoneNumber;
import com.mts.backend.domain.staff.Manager;
import com.mts.backend.domain.staff.identifier.ManagerId;

import java.util.List;
import java.util.Optional;

public interface IManagerRepository {
    
    Optional<Manager> findById(ManagerId managerId);
    
    Manager save(Manager manager);
    
    boolean existsById(ManagerId managerId);
    
    boolean existsByEmail(Email email);

    boolean existsByManagerId(ManagerId managerId);
    
    List<Manager> findAll();
    
    boolean existsByAccountId(AccountId accountId);
    
    boolean existsByPhone(PhoneNumber phoneNumber);
}
