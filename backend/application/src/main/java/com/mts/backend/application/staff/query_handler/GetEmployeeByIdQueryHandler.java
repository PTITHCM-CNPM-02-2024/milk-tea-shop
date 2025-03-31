package com.mts.backend.application.staff.query_handler;

import com.mts.backend.application.staff.query.EmployeeByIdQuery;
import com.mts.backend.application.staff.response.EmployeeDetailResponse;
import com.mts.backend.domain.account.jpa.JpaAccountRepository;
import com.mts.backend.domain.staff.EmployeeEntity;
import com.mts.backend.domain.staff.identifier.EmployeeId;
import com.mts.backend.domain.staff.jpa.JpaEmployeeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.NotFoundException;
import com.mts.backend.shared.query.IQueryHandler;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GetEmployeeByIdQueryHandler implements IQueryHandler<EmployeeByIdQuery, CommandResult> {
    private final JpaEmployeeRepository employeeRepository;
    private final JpaAccountRepository accountRepository;

    public GetEmployeeByIdQueryHandler(JpaEmployeeRepository employeeRepository, JpaAccountRepository accountRepository) {
        this.employeeRepository = employeeRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public CommandResult handle(EmployeeByIdQuery query) {
        Objects.requireNonNull(query, "Employee by id query is required");
        var employee = mustExistEmployee(query.getId());

        EmployeeDetailResponse response = EmployeeDetailResponse.builder().build();
        
        response.setId(employee.getId());
        response.setFirstName(employee.getFirstName().getValue());
        response.setLastName(employee.getLastName().getValue());
        response.setEmail(employee.getEmail().getValue());
        response.setPhone(employee.getPhone().getValue());
        response.setGender(employee.getGender().toString());
        response.setPosition(employee.getPosition().getValue());
        response.setAccountId(employee.getAccountEntity().getId());
        
        return CommandResult.success(response);
    }
    
    private EmployeeEntity mustExistEmployee(EmployeeId id){
        Objects.requireNonNull(id, "Employee id is required");
        
        return employeeRepository.findByIdWithJoinFetch(id.getValue())
                .orElseThrow(() -> new NotFoundException("Nhân viên không tồn tại"));
    }
    
}
