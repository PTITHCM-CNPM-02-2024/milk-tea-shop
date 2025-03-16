package com.mts.backend.application.staff.query_handler;

import com.mts.backend.application.staff.query.DefaultEmployeeQuery;
import com.mts.backend.application.staff.response.EmployeeDetailResponse;
import com.mts.backend.domain.staff.repository.IEmployeeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllEmployeeQueryHandler implements IQueryHandler<DefaultEmployeeQuery, CommandResult> {
    private final IEmployeeRepository employeeRepository;

    public GetAllEmployeeQueryHandler(IEmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public CommandResult handle(DefaultEmployeeQuery query) {
        Objects.requireNonNull(query);

        var employees = employeeRepository.findAll();
        List<EmployeeDetailResponse> responses = new ArrayList<>();

        employees.forEach(employee -> {
            var response = EmployeeDetailResponse.builder().build();
            response.setId(employee.getId().getValue());
            response.setFirstName(employee.getFirstName().getValue());
            response.setLastName(employee.getLastName().getValue());
            response.setEmail(employee.getEmail().getValue());
            response.setPhone(employee.getPhoneNumber().getValue());
            response.setGender(employee.getGender().toString());
            response.setPosition(employee.getPosition().getValue());
            response.setAccountId(employee.getAccountId().getValue());
            responses.add(response);
        });

        return CommandResult.success(responses);
    }
}
