package com.mts.backend.application.customer.handler;

import com.mts.backend.application.customer.command.CreateCustomerCommand;
import com.mts.backend.domain.account.Account;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.account.jpa.JpaRoleRepository;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.domain.common.value_object.Gender;
import com.mts.backend.domain.customer.Customer;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.customer.jpa.JpaCustomerRepository;
import com.mts.backend.domain.customer.jpa.JpaMembershipTypeRepository;
import com.mts.backend.domain.customer.value_object.RewardPoint;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CreateCustomerCommandHandler implements ICommandHandler<CreateCustomerCommand, CommandResult> {
    private final JpaCustomerRepository customerRepository;
    private final JpaAccountRepository accountRepository;
    private final JpaMembershipTypeRepository membershipTypeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JpaRoleRepository roleRepository;

    public CreateCustomerCommandHandler(
            JpaCustomerRepository customerRepository,
            JpaAccountRepository accountRepository,
            JpaMembershipTypeRepository membershipTypeRepository,
            PasswordEncoder passwordEncoder, JpaRoleRepository roleRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.membershipTypeRepository = membershipTypeRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public CommandResult handle(CreateCustomerCommand command) {
        Objects.requireNonNull(command, "Create customer command is required");

        try {

            var msEn = command.getMembershipId().map(e -> membershipTypeRepository.getReferenceById(e.getValue()))
                    .orElseGet(() -> {
                        var minimumMembershipType = membershipTypeRepository.findWithMinimumRequiredPoint();

                        return minimumMembershipType.orElseThrow(() -> new NotFoundException("Không tìm thấy " +
                                                                                             "chương trình khách " +
                                                                                             "hàng phù hợp"));
                    });

            Account acEn = create(command);

            Customer cus = Customer.builder()
                    .id(CustomerId.create().getValue())
                    .firstName(command.getFirstName().orElse(null))
                    .lastName(command.getLastName().orElse(null))
                    .email(command.getEmail().orElse(null))
                    .gender(command.getGender().orElse(Gender.OTHER))
                    .phone(command.getPhone())
                    .membershipType(msEn)
                    .account(acEn)
                    .currentPoint(RewardPoint.of(msEn == null ? RewardPoint.of(0).getValue() : msEn.getRequiredPoint()))
                    .build();

            var createdCustomer = customerRepository.saveAndFlush(cus);

            return CommandResult.success(createdCustomer.getId());
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("uk_customer_phone")) {
                throw new DomainException("Số điện thoại đã tồn tại");
            }
            if (e.getMessage().contains("uk_customer_email")) {
                throw new DomainException("Email đã tồn tại");
            }
            if (e.getMessage().contains("fk_customer_membership_type_id")) {
                throw new DomainException("Membership type không tồn tại");
            }
            throw new DomainException("Lỗi khi tạo khách hàng", e);
        }

    }

    private Account create(CreateCustomerCommand command) {

        if (command.getUsername().isEmpty() && command.getPasswordHash().isEmpty()) {
            return null;
        }

        try {
            var passwordHash = passwordEncoder.encode(command.getPasswordHash().get().getValue());

            var account = Account.builder()
                    .id(AccountId.create().getValue())
                    .username(command.getUsername().get())
                    .passwordHash(PasswordHash.of(passwordHash))
                    .tokenVersion(0L)
                    .active(false)
                    .locked(false)
                    .roleEntity(roleRepository.getReferenceById(command.getRoleId().getValue()))
                    .build();

            var accountEntity = accountRepository.saveAndFlush(account);

            accountRepository.grantPermissionsByRole(accountEntity.getId());

            return accountEntity;
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("uk_account_username")) {
                throw new DomainException("Tên tài khoản đã tồn tại");
            }
            if (e.getMessage().contains("fk_account_role_id")) {
                throw new DomainException("Role không tồn tại");
            }
            throw new DomainException("Lỗi khi tạo tài khoản", e);
        }
    }

}
