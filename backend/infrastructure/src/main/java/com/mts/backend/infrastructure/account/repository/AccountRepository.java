package com.mts.backend.infrastructure.account.repository;

import com.mts.backend.domain.account.Account;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.identifier.RoleId;
import com.mts.backend.domain.account.repository.IAccountRepository;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.domain.account.value_object.Username;
import com.mts.backend.infrastructure.account.jpa.JpaAccountRepository;
import com.mts.backend.infrastructure.account.jpa.JpaRoleRepository;
import com.mts.backend.infrastructure.persistence.entity.AccountEntity;
import com.mts.backend.infrastructure.persistence.entity.RoleEntity;
import com.mts.backend.shared.exception.DomainException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AccountRepository implements IAccountRepository {
    
    private final JpaAccountRepository jpaAccountRepository;

    public AccountRepository(JpaAccountRepository jpaAccountRepository, JpaRoleRepository jpaRoleRepository) {
        this.jpaAccountRepository = jpaAccountRepository;
    }


    /**
     * @param accountId 
     * @return
     */
    @Override
    public Optional<Account> findById(AccountId accountId) {
        
        Objects.requireNonNull(accountId, "Account id is required");
        
        return jpaAccountRepository.findById(accountId.getValue())
                .map(e -> new Account(
                        AccountId.of(e.getId()),
                        Username.of(e.getUsername()),
                        PasswordHash.of(e.getPasswordHash()),
                        RoleId.of(e.getRoleEntity().getId()),
                        e.getLastLogin(),
                        e.getTokenVersion(),
                        e.getIsActive(),
                        e.getIsLocked(),
                        e.getUpdatedAt().orElse(LocalDateTime.now())));
    }

    /**
     * @param account 
     * @return
     */
    @Override
    @Transactional
    public Account save(Account account) {
        Objects.requireNonNull(account, "Account is required");
        
        try {
            if (jpaAccountRepository.existsById(account.getId().getValue())) {
                return update(account);
            }
            return create(account);
        }catch (Exception e) {
            throw new DomainException("Không thể lưu tài khoản " + e.getMessage(), e);
        }
    }
    
    @Transactional
    protected Account create(Account account) {
        try {
            AccountEntity accountEntity = AccountEntity.builder()
                    .id(account.getId().getValue())
                    .username(account.getUsername().getValue())
                    .passwordHash(account.getPasswordHash().getValue())
                    .lastLogin(account.getLastLogin())
                    .tokenVersion(account.getTokenVersion())
                    .isActive(account.isActive())
                    .isLocked(account.isLocked())
                    .build();

            RoleEntity roleEntity = RoleEntity.builder()
                            .id(account.getRoleId().getValue())
                            .build();
            
            accountEntity.setRoleEntity(roleEntity);
            
            jpaAccountRepository.insertAccount(accountEntity);
            return account;
        }catch (Exception e) {
            throw e;
        }
    }
    
    
    @Transactional
    protected Account update(Account account) {
        try {
            AccountEntity accountEntity = AccountEntity.builder()
                    .id(account.getId().getValue())
                    .username(account.getUsername().getValue())
                    .passwordHash(account.getPasswordHash().getValue())
                    .lastLogin(account.getLastLogin())
                    .tokenVersion(account.getTokenVersion())
                    .isActive(account.isActive())
                    .isLocked(account.isLocked())
                    .build();
            
            RoleEntity roleEntity = RoleEntity.builder()
                            .id(account.getRoleId().getValue())
                            .build();
            
            accountEntity.setRoleEntity(roleEntity);
            
            jpaAccountRepository.updateAccount(accountEntity);
            return account;
        }catch (Exception e) {
            throw new DomainException("Không thể cập nhật tài khoản", e);
        }
    }
    /**
     * @return 
     */
    @Override
    public List<Account> findAll() {
        return jpaAccountRepository.findAll().stream()
                .map(e -> new Account(
                        AccountId.of(e.getId()),
                        Username.of(e.getUsername()),
                        PasswordHash.of(e.getPasswordHash()),
                        RoleId.of(e.getRoleEntity().getId()),
                        e.getLastLogin(),
                    e.getTokenVersion(),
                    e.getIsActive(),
                    e.getIsLocked(),
                    e.getUpdatedAt().orElse(LocalDateTime.now()))).toList();
    }

    /**
     * @param accountId 
     * @return
     */
    @Override
    public boolean existsById(AccountId accountId) {
        Objects.requireNonNull(accountId, "Account id is required");
        return jpaAccountRepository.existsById(accountId.getValue());
    }

    /**
     * @param username 
     * @return
     */
    @Override
    public boolean existsByUsername(Username username) {
        return jpaAccountRepository.existsByUsername(username.getValue());
    }

}
