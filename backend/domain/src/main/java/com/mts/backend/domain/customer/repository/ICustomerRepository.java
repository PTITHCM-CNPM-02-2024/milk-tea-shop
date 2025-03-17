package com.mts.backend.domain.customer.repository;

import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.PhoneNumber;
import com.mts.backend.domain.customer.Customer;
import com.mts.backend.domain.customer.identifier.CustomerId;

import java.util.List;
import java.util.Optional;

public interface ICustomerRepository {
    
    Optional<Customer> findById(CustomerId customerId);
    List<Customer> findAll();
    Customer save(Customer customer);
    Optional<Customer> findByPhone(PhoneNumber phoneNumber);
    Optional<Customer> findByEmail(Email email);
    
    boolean existsByPhone(PhoneNumber phoneNumber);
    boolean existsByEmail(Email email);
    
    
}
