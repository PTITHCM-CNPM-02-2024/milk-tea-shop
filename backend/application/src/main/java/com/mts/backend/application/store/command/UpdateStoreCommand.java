package com.mts.backend.application.store.command;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UpdateStoreCommand implements ICommand<CommandResult> {
    private Integer id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String taxCode;
    private LocalTime openTime;
    private LocalTime closeTime;
    private LocalDateTime openingDate;
}
