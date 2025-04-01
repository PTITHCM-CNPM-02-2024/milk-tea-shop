package com.mts.backend.application.customer.handler;

import com.mts.backend.application.customer.command.UpdateMemberCommand;
import com.mts.backend.domain.common.value_object.MemberDiscountValue;
import com.mts.backend.domain.customer.MembershipTypeEntity;
import com.mts.backend.domain.customer.identifier.MembershipTypeId;
import com.mts.backend.domain.customer.jpa.JpaMembershipTypeRepository;
import com.mts.backend.domain.customer.value_object.MemberTypeName;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateMemberCommandHandler implements ICommandHandler<UpdateMemberCommand, CommandResult> {
    private final JpaMembershipTypeRepository membershipTypeRepository;
    
    public UpdateMemberCommandHandler(JpaMembershipTypeRepository membershipTypeRepository) {
        this.membershipTypeRepository = membershipTypeRepository;
    }
    
    
    @Override
    public CommandResult handle(UpdateMemberCommand command) {
        Objects.requireNonNull(command, "Update member command is required");
        
        MembershipTypeEntity membershipType = mustExistMembershipType(command.getMemberId());
        
        if (membershipType.changeType(command.getName())) {
            verifyUniqueName(command.getMemberId(), command.getName());
        }

        MemberDiscountValue memberDiscountValue = MemberDiscountValue.builder()
                .unit(command.getDiscountUnit())
                .value(command.getDiscountValue()).build();
        
        membershipType.changeDiscountValue(memberDiscountValue);
        
        membershipType.changeRequiredPoint(command.getRequiredPoints());
        
        membershipType.changeDescription(command.getDescription());
        
        membershipType.changeValidUntil(command.getValidUntil());
        
        membershipType.setActive(command.isActive());
        
        
        
        return CommandResult.success(membershipType.getId());
        
    }
    
    private MembershipTypeEntity mustExistMembershipType(MembershipTypeId membershipTypeId) {
        return membershipTypeRepository.findById(membershipTypeId)
                .orElseThrow(() -> new NotFoundException("Loại thành viên không tồn tại"));
    }
    
    private void verifyUniqueName(MembershipTypeId id, MemberTypeName name) {
        Objects.requireNonNull(name, "Member type name is required");
        if (membershipTypeRepository.existsByIdNotAndType(id.getValue(), name)) {
            throw new DuplicateException("Tên loại thành viên đã tồn tại");
        }
    }
}
