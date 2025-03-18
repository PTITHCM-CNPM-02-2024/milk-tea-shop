package com.mts.backend.application.staff.query_handler;

import com.mts.backend.application.staff.query.EmployeeByIdQuery;
import com.mts.backend.application.staff.response.EmployeeDetailResponse;
import com.mts.backend.domain.account.Account;
import com.mts.backend.domain.account.identifier.AccountId;
import com.mts.backend.domain.account.repository.IAccountRepository;
import com.mts.backend.domain.staff.Employee;
import com.mts.backend.domain.staff.identifier.EmployeeId;
import com.mts.backend.domain.staff.repository.IEmployeeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetEmployeeByIdQueryHandler implements IQueryHandler<EmployeeByIdQuery, CommandResult> {
    private final IEmployeeRepository employeeRepository;
    private final IAccountRepository accountRepository;

    public GetEmployeeByIdQueryHandler(IEmployeeRepository employeeRepository, IAccountRepository accountRepository) {
        this.employeeRepository = employeeRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public CommandResult handle(EmployeeByIdQuery query) {
        Objects.requireNonNull(query, "Employee by id query is required");
        var employee = mustExistEmployee(EmployeeId.of(query.getId()));
        
        var account = mustExistAccount(employee.getAccountId());

        EmployeeDetailResponse response = EmployeeDetailResponse.builder().build();
        
        response.setId(employee.getId().getValue());
        response.setFirstName(employee.getFirstName().getValue());
        response.setLastName(employee.getLastName().getValue());
        response.setEmail(employee.getEmail().getValue());
        response.setPhone(employee.getPhoneNumber().getValue());
        response.setGender(employee.getGender().toString());
        response.setPosition(employee.getPosition().getValue());
        response.setAccountId(account.getId().getValue());
        
        return CommandResult.success(response);
    }
    
    private Employee mustExistEmployee(EmployeeId id){
        Objects.requireNonNull(id, "Employee id is required");
        
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Nhân viên không tồn tại"));
    }
    
    private Account mustExistAccount(AccountId accountId){
        Objects.requireNonNull(accountId, "Account id is required");
        
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Tài khoản không tồn tại"));
    }
}
