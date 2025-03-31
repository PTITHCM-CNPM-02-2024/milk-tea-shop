package com.mts.backend.application.staff.query_handler;

import com.mts.backend.application.staff.query.DefaultEmployeeQuery;
import com.mts.backend.application.staff.response.EmployeeDetailResponse;
import com.mts.backend.domain.staff.jpa.JpaEmployeeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQueryHandler;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GetAllEmployeeQueryHandler implements IQueryHandler<DefaultEmployeeQuery, CommandResult> {
    private final JpaEmployeeRepository employeeRepository;

    public GetAllEmployeeQueryHandler(JpaEmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public CommandResult handle(DefaultEmployeeQuery query) {
        Objects.requireNonNull(query);

        var employees = employeeRepository.findAllWithJoinFetch(PageRequest.of(query.getPage(), query.getSize()));
        List<EmployeeDetailResponse> responses = new ArrayList<>();

        employees.forEach(employee -> {
            var response = EmployeeDetailResponse.builder().build();
            response.setId(employee.getId());
            response.setFirstName(employee.getFirstName().getValue());
            response.setLastName(employee.getLastName().getValue());
            response.setEmail(employee.getEmail().getValue());
            response.setPhone(employee.getPhone().getValue());
            response.setGender(employee.getGender().toString());
            response.setPosition(employee.getPosition().getValue());
            response.setAccountId(employee.getAccountEntity().getId());
            responses.add(response);
        });

        return CommandResult.success(responses);
    }
}
