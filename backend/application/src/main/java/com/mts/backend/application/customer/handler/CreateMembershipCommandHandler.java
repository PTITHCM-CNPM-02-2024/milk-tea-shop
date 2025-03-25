package com.mts.backend.application.customer.handler;

import com.mts.backend.application.customer.command.CreateMembershipCommand;
import com.mts.backend.domain.common.value_object.MemberDiscountValue;
import com.mts.backend.domain.customer.MembershipType;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.repository.IMembershipTypeRepository;
import com.mts.backend.domain.common.value_object.DiscountUnit;
import com.mts.backend.domain.customer.value_object.MemberTypeName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CreateMembershipCommandHandler implements ICommandHandler<CreateMembershipCommand, CommandResult> {
    private final IMembershipTypeRepository membershipTypeRepository;

    public CreateMembershipCommandHandler(IMembershipTypeRepository membershipTypeRepository) {
        this.membershipTypeRepository = membershipTypeRepository;
    }

    @Override
    public CommandResult handle(CreateMembershipCommand command) {
        Objects.requireNonNull(command, "Create membership command is required");

        MemberDiscountValue memberDiscountValue = MemberDiscountValue.of(command.getDiscountValue(), DiscountUnit.valueOf(command.getDiscountUnit()));
        
        MemberTypeName name = MemberTypeName.of(command.getName());
        
        verifyUniqueName(name);
        
        var validUntil = command.getValidUntil() != null ? command.getValidUntil() : LocalDateTime.of(LocalDateTime.now().getYear() + 1, 1,1,0,0);
        
        MembershipType membershipType = new MembershipType(
                MembershipTypeId.create(),
                MemberTypeName.of(command.getName()),
                memberDiscountValue,
                command.getRequiredPoints(),
                command.getDescription(),
                validUntil,
                true,
                LocalDateTime.now()
        );
        
        var savedMembershipType = membershipTypeRepository.save(membershipType);
        
        return CommandResult.success(savedMembershipType.getId().getValue());
    }

    private void verifyUniqueName(MemberTypeName name) {
        Objects.requireNonNull(name, "Member type name is required");
        if (membershipTypeRepository.existsByName(name)){
            throw new DuplicateException("Tên loại thành viên đã tồn tại");
        }
    }
}
