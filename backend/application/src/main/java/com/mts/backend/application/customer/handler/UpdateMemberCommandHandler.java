package com.mts.backend.application.customer.handler;

import com.mts.backend.application.customer.command.UpdateMemberCommand;
import com.mts.backend.domain.customer.MembershipType;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.repository.IMembershipTypeRepository;
import com.mts.backend.domain.customer.value_object.DiscountUnit;
import com.mts.backend.domain.customer.value_object.DiscountValue;
import com.mts.backend.domain.customer.value_object.MemberTypeName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class UpdateMemberCommandHandler implements ICommandHandler<UpdateMemberCommand, CommandResult> {
    private final IMembershipTypeRepository membershipTypeRepository;
    
    public UpdateMemberCommandHandler(IMembershipTypeRepository membershipTypeRepository) {
        this.membershipTypeRepository = membershipTypeRepository;
    }
    
    
    @Override
    public CommandResult handle(UpdateMemberCommand command) {
        Objects.requireNonNull(command, "Update member command is required");
        
        MembershipType membershipType = mustExistMembershipType(MembershipTypeId.of(command.getMemberId()));
        
        if (membershipType.changeName(MemberTypeName.of(command.getName()))) {
            verifyUniqueName(membershipType.getName());
        }

        DiscountValue discountValue = DiscountValue.of(command.getDiscountValue(), DiscountUnit.valueOf(command.getDiscountUnit()));
        
        membershipType.changeDiscountValue(discountValue);
        
        membershipType.changeRequiredPoint(command.getRequiredPoints());
        
        membershipType.changeDescription(command.getDescription());
        
        membershipType.changeValidUntil(command.getValidUntil());
        
        membershipType.changeActive(command.isActive());
        
        
        var updatedMembershipType = membershipTypeRepository.save(membershipType);
        
        return CommandResult.success(updatedMembershipType.getId().getValue());
        
    }
    
    private MembershipType mustExistMembershipType(MembershipTypeId membershipTypeId) {
        return membershipTypeRepository.findById(membershipTypeId)
                .orElseThrow(() -> new NotFoundException("Loại thành viên không tồn tại"));
    }
    
    private void verifyUniqueName(MemberTypeName name) {
        Objects.requireNonNull(name, "Member type name is required");
        if (membershipTypeRepository.existsByName(name)){
            throw new DuplicateException("Tên loại thành viên đã tồn tại");
        }
    }
}
