package com.mts.backend.domain.staff.repository;

import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.PhoneNumber;
import com.mts.backend.domain.staff.Employee;
import com.mts.backend.domain.staff.identifier.EmployeeId;

import java.util.List;
import java.util.Optional;

public interface IEmployeeRepository {
    
    Optional<Employee> findById(EmployeeId employeeId);
    
    Employee save(Employee employee);
    
    boolean existsById(EmployeeId employeeId);
    
    boolean existsByEmail(Email email);
    
    boolean existsByPhoneNumber(PhoneNumber phoneNumber);

    List<Employee> findAll();
    
    void deleteById(EmployeeId employeeId);
    
    boolean existsByAccountId(AccountId accountId);
}
