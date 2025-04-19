package com.mts.backend.application.account.query_handler;

import com.mts.backend.application.account.query.UserInfoQueryByIdQuery;
import com.mts.backend.application.customer.response.CustomerDetailResponse;
import com.mts.backend.application.staff.response.EmployeeDetailResponse;
import com.mts.backend.application.staff.response.ManagerDetailResponse;
import com.mts.backend.domain.account.Account;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.FirstName;
import com.mts.backend.domain.common.value_object.LastName;
import com.mts.backend.domain.customer.Customer;
import com.mts.backend.domain.customer.jpa.JpaCustomerRepository;
import com.mts.backend.domain.staff.EmployeeEntity;
import com.mts.backend.domain.staff.ManagerEntity;
import com.mts.backend.domain.staff.jpa.JpaEmployeeRepository;
import com.mts.backend.domain.staff.jpa.JpaManagerRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class GetUserInfoByIdQueryHandler implements IQueryHandler<UserInfoQueryByIdQuery, CommandResult> {
    private final JpaAccountRepository accountRepository;
    private final JpaEmployeeRepository employeeRepository;
    private final JpaManagerRepository managerRepository;
    private final JpaCustomerRepository customerRepository;
    
    public GetUserInfoByIdQueryHandler(JpaAccountRepository accountRepository,
                                       JpaEmployeeRepository employeeRepository,
                                       JpaManagerRepository managerRepository,
                                       JpaCustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.employeeRepository = employeeRepository;
        this.managerRepository = managerRepository;
        this.customerRepository = customerRepository;
    }
    /**
     * @param query 
     * @return
     */
    @Override
    public CommandResult handle(UserInfoQueryByIdQuery query) {
        Objects.requireNonNull(query, "Query cannot be null");
        
        var account = accountRepository.findById(query.getId().getValue());
        
        if (account.isEmpty()) {
            return CommandResult.notFoundFail("Không tìm thấy tài khoản");
        }
        
        
        var role = account.get().getRoleEntity().getId();
        
        return switch (role) {
            case  (1) -> {
                var manager = getManagerById(AccountId.of(query.getId().getValue()));
                if (manager.isEmpty()) {
                    yield CommandResult.notFoundFail("Khônng tìm thấy thông tin người dùng");
                }
                var managerResponse = ManagerDetailResponse.builder()
                        .id(manager.get().getId())
                        .firstName(manager.get().getFirstName().getValue())
                        .lastName(manager.get().getLastName().getValue())
                        .email(manager.get().getEmail().getValue())
                        .accountId(manager.get().getAccountEntity().getId())
                        .gender(manager.get().getGender().name())
                        .phone(manager.get().getPhone().getValue())
                        .build();
                yield CommandResult.success(managerResponse);
            }
            case 2 -> {
                var employee = getEmployeeById(AccountId.of(query.getId().getValue()));
                if (employee.isEmpty()) {
                    yield CommandResult.notFoundFail("Không tìm thấy nhân viên");
                }
                
                var empResponse = EmployeeDetailResponse.builder()
                        .id(employee.get().getId())
                        .firstName(employee.get().getFirstName().getValue())
                        .lastName(employee.get().getLastName().getValue())
                        .email(employee.get().getEmail().getValue())
                        .accountId(employee.get().getAccount().getId())
                        .gender(employee.get().getGender().name())
                        .phone(employee.get().getPhone().getValue())
                        .position(employee.get().getPosition().getValue())
                        .build();
                yield CommandResult.success(empResponse);
            }
            case 3 -> {
                var customer = getCustomerById(AccountId.of(query.getId().getValue()));
                if (customer.isEmpty()) {
                    yield  CommandResult.notFoundFail("Không tìm thấy khách hàng");
                }
                
                var cusResponse = CustomerDetailResponse.builder()
                        .id(customer.get().getId())
                        .firstName(customer.get().getFirstName().map(FirstName::getValue).orElse(null))
                        .lastName(customer.get().getLastName().map(LastName::getValue).orElse(null))
                        .email(customer.get().getEmail().map(Email::getValue).orElse(null))
                        .phone(customer.get().getPhone().getValue())
                        .membershipId(customer.get().getMembershipTypeEntity().getId())
                        .rewardPoint(customer.get().getCurrentPoints().getValue())
                        .accountId(customer.get().getAccount().map(Account::getId).orElse(null))
                        .build();
                yield CommandResult.success(cusResponse);
            }
            case null -> CommandResult.notFoundFail("Không tìm thấy tài khoản");
            default -> CommandResult.notFoundFail("Không tìm thấy tài khoản");
        };
    }
    
    
    private Optional<ManagerEntity> getManagerById(AccountId id) {
        return managerRepository.findByAccountEntity_Id(id.getValue());
    }
    
    private Optional<Customer> getCustomerById(AccountId id) {
        return customerRepository.findByAccountEntity_Id(id.getValue());
    }
    
    private Optional<EmployeeEntity> getEmployeeById(AccountId id) {
        return employeeRepository.findByAccountEntity_Id(id.getValue());
    }
}
