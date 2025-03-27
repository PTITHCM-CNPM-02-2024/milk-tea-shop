package com.mts.backend.application.store.command;

import com.mts.backend.domain.common.value_object.Email;
import com.mts.backend.domain.common.value_object.PhoneNumber;
import com.mts.backend.domain.store.identifier.StoreId;
import com.mts.backend.domain.store.value_object.Address;
import com.mts.backend.domain.store.value_object.StoreName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UpdateStoreCommand implements ICommand<CommandResult> {
    private StoreId id;
    private StoreName name;
    private Address address;
    private PhoneNumber phone;
    private Email email;
    private String taxCode;
    private LocalTime openTime;
    private LocalTime closeTime;
    private LocalDate openingDate;
}
