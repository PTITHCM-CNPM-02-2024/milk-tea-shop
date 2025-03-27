package com.mts.backend.domain.staff.repository;

import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.FirstName;
import com.mts.backend.domain.common.value_object.LastName;
import com.mts.backend.domain.common.value_object.PhoneNumber;
import com.mts.backend.domain.staff.Employee;
import com.mts.backend.domain.staff.identifier.EmployeeId;
import com.mts.backend.domain.staff.value_object.Position;
import com.mts.backend.domain.persistence.entity.AccountEntity;
import com.mts.backend.domain.persistence.entity.EmployeeEntity;
import com.mts.backend.domain.staff.jpa.JpaEmployeeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EmployeeRepository implements IEmployeeRepository {
    
    private final JpaEmployeeRepository jpaEmployeeRepository;
    
    public EmployeeRepository(JpaEmployeeRepository jpaEmployeeRepository) {
        this.jpaEmployeeRepository = jpaEmployeeRepository;
    }
    /**
     * @param employeeId 
     * @return
     */
    @Override
    public Optional<Employee> findById(EmployeeId employeeId) {
        Objects.requireNonNull(employeeId, "Employee id is required");
        
        return jpaEmployeeRepository.findById(employeeId.getValue())
                .map(e -> new Employee(
                        EmployeeId.of(e.getId()),
                        FirstName.of(e.getFirstName()),
                        LastName.of(e.getLastName()),
                        Email.of(e.getEmail()),
                        PhoneNumber.of(e.getPhone()),
                        Position.of(e.getPosition()),
                        e.getGender(),
                        AccountId.of(e.getAccountEntity().getId()),
                        e.getUpdatedAt().orElse(LocalDateTime.now())));
    }

    /**
     * @param employee 
     * @return
     */
    @Override
    public Employee save(Employee employee) {
        Objects.requireNonNull(employee, "Employee is required");
        try {
            if (jpaEmployeeRepository.existsById(employee.getId().getValue())){
                return update(employee);
            }
            return create(employee);
        }catch (Exception e){
            throw e;
        }
    }
    
    private Employee update(Employee employee) {
        Objects.requireNonNull(employee, "Employee is required");

        EmployeeEntity em = EmployeeEntity.builder().
                            id(employee.getId().getValue()).
                            firstName(employee.getFirstName().getValue()).
                            lastName(employee.getLastName().getValue()).
                            email(employee.getEmail().getValue()).
                phone(employee.getPhoneNumber().getValue()).
                position(employee.getPosition().getValue()).
                gender(employee.getGender()).build();
        
        AccountEntity accountEntity = AccountEntity.builder().id(employee.getAccountId().getValue()).build();
        
        em.setAccountEntity(accountEntity);
        
        jpaEmployeeRepository.updateEmployee(em);
        
        return employee;
    }
    
    private Employee create(Employee employee) {
        Objects.requireNonNull(employee, "Employee is required");

        EmployeeEntity em = EmployeeEntity.builder().
                            id(employee.getId().getValue()).
                            firstName(employee.getFirstName().getValue()).
                            lastName(employee.getLastName().getValue()).
                            email(employee.getEmail().getValue()).
                gender(employee.getGender()).       
                phone(employee.getPhoneNumber().getValue()).
                position(employee.getPosition().getValue()).build();
        
        AccountEntity accountEntity = AccountEntity.builder().id(employee.getAccountId().getValue()).build();
        
        em.setAccountEntity(accountEntity);
        
        jpaEmployeeRepository.insertEmployee(em);
        
        return employee;
    }

    /**
     * @param employeeId 
     * @return
     */
    @Override
    public boolean existsById(EmployeeId employeeId) {
        Objects.requireNonNull(employeeId, "Employee id is required");
        
        return jpaEmployeeRepository.existsById(employeeId.getValue());
    }

    /**
     * @param email 
     * @return
     */
    @Override
    public boolean existsByEmail(Email email) {
        Objects.requireNonNull(email, "Email is required");
        
        return jpaEmployeeRepository.existsByEmail(email.getValue());
    }

    /**
     * @param phoneNumber 
     * @return
     */
    @Override
    public boolean existsByPhoneNumber(PhoneNumber phoneNumber) {
        Objects.requireNonNull(phoneNumber, "Phone number is required");
        
        return jpaEmployeeRepository.existsByPhone(phoneNumber.getValue());
    }

    /**
     * @return 
     */
    @Override
    public List<Employee> findAll() {
        return jpaEmployeeRepository.findAll().stream().map(e -> new Employee(
                EmployeeId.of(e.getId()),
                FirstName.of(e.getFirstName()),
                LastName.of(e.getLastName()),
                Email.of(e.getEmail()),
                PhoneNumber.of(e.getPhone()),
                Position.of(e.getPosition()),
                e.getGender(),
                AccountId.of(e.getAccountEntity().getId()),
                e.getUpdatedAt().orElse(LocalDateTime.now())
        )).toList();
    }

    /**
     * @param employeeId 
     */
    @Override
    public void deleteById(EmployeeId employeeId) {

    }

    /**
     * @param accountId 
     * @return
     */
    @Override
    public boolean existsByAccountId(AccountId accountId) {
        Objects.requireNonNull(accountId, "Account id is required");
        
        return jpaEmployeeRepository.existsByAccountId(accountId.getValue());
    }
}
