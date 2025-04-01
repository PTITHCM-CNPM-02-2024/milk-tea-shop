package com.mts.backend.application.customer.query;

import com.mts.backend.domain.common.value_object.PhoneNumber;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CustomerByPhoneQuery implements IQuery<CommandResult> {
    private PhoneNumber phone;
}
