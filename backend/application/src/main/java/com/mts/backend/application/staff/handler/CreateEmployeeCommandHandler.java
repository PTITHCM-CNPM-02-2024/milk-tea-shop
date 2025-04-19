package com.mts.backend.application.staff.handler;

import com.mts.backend.application.staff.command.CreateEmployeeCommand;
import com.mts.backend.domain.account.Account;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.account.jpa.JpaRoleRepository;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.domain.staff.EmployeeEntity;
import com.mts.backend.domain.staff.identifier.EmployeeId;
import com.mts.backend.domain.staff.jpa.JpaEmployeeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreateEmployeeCommandHandler implements ICommandHandler<CreateEmployeeCommand, CommandResult> {
    private final JpaEmployeeRepository employeeRepository;
    private final JpaAccountRepository accountRepository;
    private final JpaRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateEmployeeCommandHandler(JpaEmployeeRepository employeeRepository, JpaAccountRepository accountRepository, JpaRoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public CommandResult handle(CreateEmployeeCommand command) {

        var account = createAccount(command);

        var em = EmployeeEntity.
                builder()
                .id(EmployeeId.create().getValue())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .phone(command.getPhone())
                .email(command.getEmail())
                .position(command.getPosition())
                .gender(command.getGender())
                .accountEntity(account)
                .build();

        try {
            var result = employeeRepository.save(em);
            return CommandResult.success(result.getId());
        }catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("Duplicate entry") &&
                e.getMessage().contains("uk_employee_email")) {
                throw new DuplicateException("Email đã tồn tại");
            }
            if (e.getMessage().contains("Duplicate entry") &&
                e.getMessage().contains("uk_employee_phone")) {
                throw new DuplicateException("Số điện thoại đã tồn tại");
            }
            if (e.getMessage().contains("Duplicate entry") &&
                e.getMessage().contains("uk_employee_account_id")) {
                throw new DuplicateException("Tài khoản đã tồn tại");
            }
            if (e.getMessage().contains("not found") &&
                e.getMessage().contains("fk_employee_account")) {
                throw new NotFoundException("Không tìm thấy tài khoản");
            }
            throw new DomainException("Không thể tạo nhân viên", e);
        }
    }

    private Account createAccount(CreateEmployeeCommand command) {
        try {
            var password = PasswordHash.of(passwordEncoder.encode(command.getPassword().getValue()));

            var account = Account.builder()
                    .id(AccountId.create().getValue())
                    .username(command.getUsername())
                    .passwordHash(password)
                    .locked(false)
                    .active(false)
                    .lastLogin(null)
                    .tokenVersion(0L)
                    .roleEntity(roleRepository.getReferenceById(command.getRoleId().getValue()))
                    .build();

            accountRepository.saveAndFlush(account);

            accountRepository.grantPermissionsByRole(account.getId());

            return account;
        } catch (DataIntegrityViolationException | EntityNotFoundException e) {
            /*
             * Check if the error is due to a duplicate entry for the username
             * or if the role ID does not exist in the database.
             */
            if (e.getMessage().contains("Duplicate entry") &&
                e.getMessage().contains("uk_account_username")) {
                throw new DuplicateException("Tên đăng nhập đã tồn tại");
            }
            if (e.getMessage().contains("not found") &&
                e.getMessage().contains("fk_account_role")) {
                throw new NotFoundException("Không tìm thấy vai trò");
            }
            throw new DomainException("Không thể tạo tài khoản", e);
        }
    }


}
