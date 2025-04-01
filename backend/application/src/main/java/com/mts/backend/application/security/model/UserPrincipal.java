package com.mts.backend.application.security.model;

import com.mts.backend.domain.account.AccountEntity;
import com.mts.backend.domain.account.identifier.AccountId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UserPrincipal implements UserDetails {
    private final AccountEntity account;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(AccountEntity account) {
        this.account = account;
        this.authorities =
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + account.getRoleEntity().getName().getValue().toUpperCase()));
    }
    
    public Long getTokenVersion() {
        return account.getTokenVersion();
    }
    
    public AccountId getId() {
        return AccountId.of(account.getId());
    }

    /**
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * @return
     */
    @Override
    public String getPassword() {
        return account.getPasswordHash().getValue();
    }

    /**
     * @return
     */
    @Override
    public String getUsername() {
        return account.getUsername().getValue();
    }

    /**
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    /**
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return !account.getLocked();
    }

    /**
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    /**
     * @return
     */
    @Override
    public boolean isEnabled() {
        return account.getActive().orElse(false);
    }
}
