package com.mts.backend.infrastructure.security;

import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.account.value_object.Username;
import com.mts.backend.application.security.model.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JwtUserDetailService implements UserDetailsService {
    private final JpaAccountRepository accountRepository;

    public JwtUserDetailService(JpaAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var account = accountRepository.findByUsername(Username.of(username).getValue())
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản với tên đăng nhập: " + username));
        
        account.login();
        
        return new UserPrincipal(account);
    }
}
