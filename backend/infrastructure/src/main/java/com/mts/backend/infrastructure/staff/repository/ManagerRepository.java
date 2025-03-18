package com.mts.backend.infrastructure.staff.repository;

import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.FirstName;
import com.mts.backend.domain.common.value_object.LastName;
import com.mts.backend.domain.common.value_object.PhoneNumber;
import com.mts.backend.domain.staff.Manager;
import com.mts.backend.domain.staff.identifier.ManagerId;
import com.mts.backend.domain.staff.repository.IManagerRepository;
import com.mts.backend.infrastructure.persistence.entity.AccountEntity;
import com.mts.backend.infrastructure.persistence.entity.ManagerEntity;
import com.mts.backend.infrastructure.staff.jpa.JpaManagerRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
@Service
public class ManagerRepository implements IManagerRepository {
    
    private final JpaManagerRepository jpaManagerRepository;
    
    public ManagerRepository(JpaManagerRepository jpaManagerRepository) {
        this.jpaManagerRepository = jpaManagerRepository;
    }
    /**
     * @param managerId 
     * @return
     */
    @Override
    public Optional<Manager> findById(ManagerId managerId) {
        Objects.requireNonNull(managerId, "Manager id is required");
        
        return jpaManagerRepository.findById(managerId.getValue())
                .map(e -> new Manager(
                        ManagerId.of(e.getId()),
                        FirstName.of(e.getFirstName()),
                        LastName.of(e.getLastName()),
                        Email.of(e.getEmail()),
                        PhoneNumber.of(e.getPhone()),
                        e.getGender(),
                        AccountId.of(e.getAccountEntity().getId()),
                        e.getUpdatedAt().orElse(LocalDateTime.now())));
    }

    /**
     * @param manager 
     * @return
     */
    @Override
    @Transactional
    public Manager save(Manager manager) {
        Objects.requireNonNull(manager, "Manager is required");
        
        try {
            if (jpaManagerRepository.existsById(manager.getId().getValue())){
                return update(manager);
            }
            return create(manager);
        }catch (Exception e){
            throw e;    
        }
    }
    
    @Transactional
    protected Manager create(Manager manager){
        Objects.requireNonNull(manager, "Manager is required");

        ManagerEntity managerEntity = ManagerEntity.builder()
                .id(manager.getId().getValue())
                .firstName(manager.getFirstName().getValue())
                .lastName(manager.getLastName().getValue())
                .email(manager.getEmail().getValue())
                .gender(manager.getGender())
                .phone(manager.getPhoneNumber().getValue())
                .build();

        AccountEntity accountEntity = AccountEntity.builder()
                .id(manager.getAccountId().getValue())
                .build();
        
        managerEntity.setAccountEntity(accountEntity);
        
        jpaManagerRepository.insertManager(managerEntity);
        
        return manager;
        
        
    }
    
    @Transactional
    protected Manager update (Manager manager){
        Objects.requireNonNull(manager, "Manager is required");
        
        ManagerEntity managerEntity = ManagerEntity.builder()
                .id(manager.getId().getValue())
                .firstName(manager.getFirstName().getValue())
                .lastName(manager.getLastName().getValue())
                .email(manager.getEmail().getValue())
                .phone(manager.getPhoneNumber().getValue())
                .gender(manager.getGender())
                .build();
        
        AccountEntity accountEntity = AccountEntity.builder()
                .id(manager.getAccountId().getValue())
                .build();
        
        managerEntity.setAccountEntity(accountEntity);
        
        jpaManagerRepository.updateManager(managerEntity);
        
        return manager;
    }

    /**t
     * @param managerId 
     * @return
     */
    @Override
    public boolean existsById(ManagerId managerId) {
        Objects.requireNonNull(managerId, "Manager id is required");
        
        return jpaManagerRepository.existsById(managerId.getValue());
    }

    /**
     * @param email 
     * @return
     */
    @Override
    public boolean existsByEmail(Email email) {
        Objects.requireNonNull(email, "Email is required");
        
        return jpaManagerRepository.existsByEmail(email.getValue());
    }

    /**
     * @param managerId 
     * @return
     */
    @Override
    public boolean existsByManagerId(ManagerId managerId) {
        Objects.requireNonNull(managerId, "Manager id is required");
        
        return jpaManagerRepository.existsById(managerId.getValue());
    }

    /**
     * @return 
     */
    @Override
    public List<Manager> findAll() {
        return jpaManagerRepository.findAll().stream()
                .map(e -> new Manager(
                        ManagerId.of(e.getId()),
                        FirstName.of(e.getFirstName()),
                        LastName.of(e.getLastName()),
                        Email.of(e.getEmail()),
                        PhoneNumber.of(e.getPhone()),
                        e.getGender(),
                        AccountId.of(e.getAccountEntity().getId()),
                        e.getUpdatedAt().orElse(LocalDateTime.now()))).toList();
    }

    /**
     * @param accountId 
     * @return
     */
    @Override
    public boolean existsByAccountId(AccountId accountId) {
        Objects.requireNonNull(accountId, "Account id is required");
        return jpaManagerRepository.existsByAccountId(accountId.getValue());
    }

    /**
     * @param phoneNumber 
     * @return
     */
    @Override
    public boolean existsByPhone(PhoneNumber phoneNumber) {
        Objects.requireNonNull(phoneNumber, "Phone number is required");
        
        return jpaManagerRepository.existsByPhone(phoneNumber.getValue());
    }
}
