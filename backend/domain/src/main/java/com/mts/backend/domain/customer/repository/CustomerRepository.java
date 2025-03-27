package com.mts.backend.domain.customer.repository;

import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.common.value_object.*;
import com.mts.backend.domain.customer.Customer;
import com.mts.backend.domain.customer.identifier.CustomerId;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.value_object.RewardPoint;
import com.mts.backend.domain.customer.jpa.JpaCustomerRepository;
import com.mts.backend.domain.persistence.entity.AccountEntity;
import com.mts.backend.domain.persistence.entity.CustomerEntity;
import com.mts.backend.domain.persistence.entity.MembershipTypeEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CustomerRepository implements ICustomerRepository {

    private final JpaCustomerRepository jpaCustomerRepository;

    public CustomerRepository(JpaCustomerRepository jpaCustomerRepository) {
        this.jpaCustomerRepository = jpaCustomerRepository;
    }

    /**
     * @param customerId
     * @return
     */
    @Override
    public Optional<Customer> findById(CustomerId customerId) {
        Objects.requireNonNull(customerId, "Customer id is required");

        return jpaCustomerRepository.findById(customerId.getValue())
                .map(entity -> new Customer(
                        CustomerId.of(entity.getId()),
                        entity.getFirstName() != null ? FirstName.of(entity.getFirstName()) : null,
                        entity.getLastName() != null ? LastName.of(entity.getLastName()) : null,
                        PhoneNumber.of(entity.getPhone()),
                        entity.getEmail() != null ? Email.of(entity.getEmail()) : null,
                        entity.getGender() != null ? Gender.valueOf(entity.getGender().name()) : null,
                        RewardPoint.of(entity.getCurrentPoints()),
                        MembershipTypeId.of(entity.getMembershipTypeEntity().getId()),
                        entity.getAccountEntity() != null ? AccountId.of(entity.getAccountEntity().getId()) : null,
                        entity.getUpdatedAt().orElse(LocalDateTime.now())));
    }

    /**
     * @return
     */
    @Override
    public List<Customer> findAll() {
        return jpaCustomerRepository.findAll().stream()
                .map(entity -> new Customer(
                        CustomerId.of(entity.getId()),
                        entity.getFirstName() != null ? FirstName.of(entity.getFirstName()) : null,
                        entity.getLastName() != null ? LastName.of(entity.getLastName()) : null,
                        PhoneNumber.of(entity.getPhone()),
                        entity.getEmail() != null ? Email.of(entity.getEmail()) : null,
                        entity.getGender() != null ? Gender.valueOf(entity.getGender().name()) : null,
                        RewardPoint.of(entity.getCurrentPoints()),
                        MembershipTypeId.of(entity.getMembershipTypeEntity().getId()),
                        entity.getAccountEntity() != null ? AccountId.of(entity.getAccountEntity().getId()) : null,
                        entity.getUpdatedAt().orElse(LocalDateTime.now()))).toList();
    }

    /**
     * @param customer
     */
    @Override
    @Transactional
    public Customer save(Customer customer) {
        Objects.requireNonNull(customer, "Customer is required");

        try {
            if (jpaCustomerRepository.existsById(customer.getId().getValue())) {
                return update(customer);
            }
            return create(customer);
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional
    protected Customer create(Customer customer) {
        Objects.requireNonNull(customer, "Customer is required");

        CustomerEntity cus = CustomerEntity.builder()
                .id(customer.getId().getValue())
                .firstName(customer.getFirstName().map(FirstName::getValue).orElse(null))
                .lastName(customer.getLastName().map(LastName::getValue).orElse(null))
                .phone(customer.getPhoneNumber().getValue())
                .email(customer.getEmail().map(Email::getValue).orElse(null))
                .currentPoints(customer.getRewardPoint().getValue())
                .gender(customer.getGender().map(e -> Gender.valueOf(e.toString())).orElse(null))
                .build();
        AccountEntity account = AccountEntity.builder()
                .id(customer.getAccountId().map(AccountId::getValue).orElse(null))
                .build();
        MembershipTypeEntity mem = MembershipTypeEntity.builder()
                .id(customer.getMembershipTypeId().getValue()).build();
        cus.setMembershipTypeEntity(mem);
        cus.setAccountEntity(account);

        jpaCustomerRepository.insertCustomer(cus);

        return customer;

    }


    @Transactional
    protected Customer update(Customer customer) {
        Objects.requireNonNull(customer, "Customer is required");

        CustomerEntity cus = CustomerEntity.builder()
                .id(customer.getId().getValue())
                .firstName(customer.getFirstName().map(FirstName::getValue).orElse(null))
                .lastName(customer.getLastName().map(LastName::getValue).orElse(null))
                .phone(customer.getPhoneNumber().getValue())
                .email(customer.getEmail().map(Email::getValue).orElse(null))
                .currentPoints(customer.getRewardPoint().getValue())
                .gender(customer.getGender().map(e -> Gender.valueOf(e.name())).orElse(null))
                .build();
        AccountEntity account = AccountEntity.builder()
                .id(customer.getAccountId().map(AccountId::getValue).orElse(null))
                .build();
        MembershipTypeEntity mem = MembershipTypeEntity.builder()
                .id(customer.getMembershipTypeId().getValue()).build();
        cus.setMembershipTypeEntity(mem);
        cus.setAccountEntity(account);

        jpaCustomerRepository.updateCustomer(cus);

        return customer;

    }


    /**
     * @param phoneNumber
     * @return
     */
    @Override
    public Optional<Customer> findByPhone(PhoneNumber phoneNumber) {
        Objects.requireNonNull(phoneNumber, "Phone number is required");
        return jpaCustomerRepository.findByPhone(phoneNumber.getValue())
                .map(entity -> new Customer(
                        CustomerId.of(entity.getId()),
                        entity.getFirstName() != null ? FirstName.of(entity.getFirstName()) : null,
                        entity.getLastName() != null ? LastName.of(entity.getLastName()) : null,
                        PhoneNumber.of(entity.getPhone()),
                        entity.getEmail() != null ? Email.of(entity.getEmail()) : null,
                        entity.getGender() != null ? Gender.valueOf(entity.getGender().name()) : null,
                        RewardPoint.of(entity.getCurrentPoints()),
                        MembershipTypeId.of(entity.getMembershipTypeEntity().getId()),
                        entity.getAccountEntity() != null ? AccountId.of(entity.getAccountEntity().getId()) : null,
                        entity.getUpdatedAt().orElse(LocalDateTime.now())));

    }

    /**
     * @param email
     * @return
     */
    @Override
    public Optional<Customer> findByEmail(Email email) {
        Objects.requireNonNull(email, "Email is required");

        return jpaCustomerRepository.findByEmail(email.getValue())
                .map(entity -> new Customer(
                        CustomerId.of(entity.getId()),
                        entity.getFirstName() != null ? FirstName.of(entity.getFirstName()) : null,
                        entity.getLastName() != null ? LastName.of(entity.getLastName()) : null,
                        PhoneNumber.of(entity.getPhone()),
                        entity.getEmail() != null ? Email.of(entity.getEmail()) : null,
                        entity.getGender() != null ? Gender.valueOf(entity.getGender().name()) : null,
                        RewardPoint.of(entity.getCurrentPoints()),
                        MembershipTypeId.of(entity.getMembershipTypeEntity().getId()),
                        entity.getAccountEntity() != null ? AccountId.of(entity.getAccountEntity().getId()) : null,
                        entity.getUpdatedAt().orElse(LocalDateTime.now())));
    }

    /**
     * @param phoneNumber
     * @return
     */
    @Override
    public boolean existsByPhone(PhoneNumber phoneNumber) {
        Objects.requireNonNull(phoneNumber, "Phone number is required");
        return jpaCustomerRepository.existsByPhone(phoneNumber.getValue());
    }

    /**
     * @param email
     * @return
     */
    @Override
    public boolean existsByEmail(Email email) {
        Objects.requireNonNull(email, "Email is required");
        return jpaCustomerRepository.existsByEmail(email.getValue());
    }
}
