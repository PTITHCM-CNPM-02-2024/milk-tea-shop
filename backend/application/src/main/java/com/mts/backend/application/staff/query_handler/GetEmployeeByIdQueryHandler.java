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
    public GetEmployeeByIdQueryHandler(JpaEmployeeRepository employeeRepository, JpaAccountRepository accountRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public CommandResult handle(EmployeeByIdQuery query) {
        Objects.requireNonNull(query, "Employee by id query is required");
        var employee = mustExistEmployee(query.getId());

        var response = EmployeeDetailResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName().getValue())
                .lastName(employee.getLastName().getValue())
                .email(employee.getEmail().getValue())
                .phone(employee.getPhone().getValue())
                .gender(employee.getGender().toString())
                .position(employee.getPosition().getValue())
                .accountId(employee.getAccountEntity().getId())
                .build();
        
        return CommandResult.success(response);
    }
    
    private EmployeeEntity mustExistEmployee(EmployeeId id){
        Objects.requireNonNull(id, "Employee id is required");
        
        return employeeRepository.findByIdWithJoinFetch(id.getValue())
                .orElseThrow(() -> new NotFoundException("Nhân viên không tồn tại"));
    }
    
}
