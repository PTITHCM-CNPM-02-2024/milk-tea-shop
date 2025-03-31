package com.mts.backend.application.customer.handler;

import com.mts.backend.application.customer.command.CreateMembershipCommand;
import com.mts.backend.domain.common.value_object.MemberDiscountValue;
import com.mts.backend.domain.customer.MembershipTypeEntity;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.jpa.JpaMembershipTypeRepository;
import com.mts.backend.domain.customer.value_object.MemberTypeName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CreateMembershipCommandHandler implements ICommandHandler<CreateMembershipCommand, CommandResult> {
    private final JpaMembershipTypeRepository membershipTypeRepository;

    public CreateMembershipCommandHandler(JpaMembershipTypeRepository membershipTypeRepository) {
        this.membershipTypeRepository = membershipTypeRepository;
    }

    @Override
    public CommandResult handle(CreateMembershipCommand command) {
        Objects.requireNonNull(command, "Create membership command is required");

        MemberDiscountValue memberDiscountValue = MemberDiscountValue.builder()
                .unit(command.getDiscountUnit())
                .value(command.getDiscountValue()).build();
        
        verifyUniqueName(command.getName());
        
        var validUntil = command.getValidUntil().isPresent() ? command.getValidUntil().get() :
                LocalDateTime.of(LocalDateTime.now().getYear() + 1, 1,1,0,0);
        
        MembershipTypeEntity membershipType = MembershipTypeEntity.builder()
                .id(MembershipTypeId.create().getValue())
                .type(command.getName())
                .description(command.getDescription())
                .memberDiscountValue(memberDiscountValue)
                .requiredPoint(command.getRequiredPoints())
                .validUntil(validUntil)
                .active(true)
                .build();
        
        verifyUniqueRewardPoint(command.getRequiredPoints());
        
        var savedMembershipType = membershipTypeRepository.save(membershipType);
        
        return CommandResult.success(savedMembershipType.getId());
    }

    private void verifyUniqueName(MemberTypeName name) {
        Objects.requireNonNull(name, "Member type name is required");
        if (membershipTypeRepository.existsByType(name)) {
            throw new DuplicateException("Tên loại thành viên đã tồn tại");
        }
    }
    
    private void verifyUniqueRewardPoint(int requiredPoints) {
        if (membershipTypeRepository.existsByRequiredPoint(requiredPoints)) {
            throw new DuplicateException("Điểm thưởng đã tồn tại");
        }
    }
    
}
