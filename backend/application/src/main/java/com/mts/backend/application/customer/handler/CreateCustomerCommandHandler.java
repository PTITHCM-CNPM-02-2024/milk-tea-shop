package com.mts.backend.application.customer.handler;

import com.mts.backend.application.customer.command.CreateCustomerCommand;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.account.jpa.JpaRoleRepository;
import com.mts.backend.domain.account.value_object.PasswordHash;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.customer.CustomerEntity;
import com.mts.backend.domain.customer.MembershipTypeEntity;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.jpa.JpaCustomerRepository;
import com.mts.backend.domain.customer.jpa.JpaMembershipTypeRepository;
import com.mts.backend.domain.customer.value_object.RewardPoint;
import com.mts.backend.domain.account.AccountEntity;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DomainException;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
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
        
        PhoneNumber phoneNumber = command.getPhone();
        verifyUniquePhoneNumber(phoneNumber);
        
        command.getEmail().ifPresent(this::mustUniqueEmail);
        
        MembershipTypeEntity msEn = verifyMemberShip(command.getMembershipId().orElse(null));
        
        AccountEntity acEn = create(command);

        CustomerEntity cus = CustomerEntity.builder()
                .id(CustomerId.create().getValue())
                .firstName(command.getFirstName().orElse(null))
                .lastName(command.getLastName().orElse(null))
                .email(command.getEmail().orElse(null))
                .gender(command.getGender().orElse(Gender.OTHER))
                .phone(command.getPhone())
                .membershipTypeEntity(msEn)
                .accountEntity(acEn)
                .currentPoints(RewardPoint.builder().value(msEn.getRequiredPoint()).build()
                )
                .build();
        
        var createdCustomer = customerRepository.save(cus);
        
        return CommandResult.success("createdCustomer.getId()");
        
    }
    
    private AccountEntity create(CreateCustomerCommand command){
        
        if (command.getUsername().isEmpty() && command.getPasswordHash().isEmpty()){
            return null;
        }
        
        if(!roleRepository.existsById(command.getRoleId().getValue())){
            throw new DomainException("Role không tồn tại");
        }
        
        var passwordHash = passwordEncoder.encode(command.getPasswordHash().get().getValue());
        
        var account = AccountEntity.builder()
                .id(AccountId.create().getValue())
                .username(command.getUsername().get())
                .passwordHash(PasswordHash.builder().value(passwordHash).build())
                .tokenVersion(0L)
                .active(false)
                .locked(false)
                .roleEntity(roleRepository.getReferenceById(command.getRoleId().getValue()))
                .build();
        
        var accountEntity = accountRepository.saveAndFlush(account);
        
        accountRepository.grantPermissionsByRole(accountEntity.getId());
        
        return accountEntity;
    }
    
    private MembershipTypeEntity verifyMemberShip(MembershipTypeId id){
        if (id == null){
            return membershipTypeRepository.findWithMinimumRequiredPoint().orElseThrow(() -> new NotFoundException(
                    "Không tồn tại một membership type nào"));
        }
        
        if (!membershipTypeRepository.existsById(id.getValue())){
            throw new NotFoundException("Membership type không tồn tại");
        }
        
        return membershipTypeRepository.getReferenceById(id.getValue());
    }
    
    private void verifyUniquePhoneNumber(PhoneNumber phoneNumber){
        Objects.requireNonNull(phoneNumber, "Phone number is required");
        if (customerRepository.existsByPhone(phoneNumber)){
            throw new NotFoundException("Số điện thoại đã tồn tại");
        }
    }
    
    private void mustUniqueEmail(Email email){
        Objects.requireNonNull(email, "Email is required");
        if (customerRepository.existsByEmail(email)){
            throw new NotFoundException("Email đã tồn tại");
        }
    }
}
