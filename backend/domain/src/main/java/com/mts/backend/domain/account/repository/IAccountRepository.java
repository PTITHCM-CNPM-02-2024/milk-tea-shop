package com.mts.backend.domain.account.repository;

import com.mts.backend.domain.account.Account;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.value_object.Username;

import java.util.List;
import java.util.Optional;

public interface IAccountRepository {
    
    Optional<Account> findById(AccountId accountId);
    
    Account save(Account account);
    
    List<Account> findAll();
    
    boolean existsById(AccountId accountId);
    
    boolean existsByUsername(Username username);

}
