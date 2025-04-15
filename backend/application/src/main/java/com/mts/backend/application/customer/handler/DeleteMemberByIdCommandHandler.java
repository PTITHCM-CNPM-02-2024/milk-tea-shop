package com.mts.backend.application.customer.handler;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mts.backend.application.customer.command.DeleteMemberByIdCommand;
import com.mts.backend.domain.customer.jpa.JpaMembershipTypeRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;

@Service
public class DeleteMemberByIdCommandHandler implements ICommandHandler<DeleteMemberByIdCommand, CommandResult> {

    private final JpaMembershipTypeRepository jpaMembershipTypeRepository;

    public DeleteMemberByIdCommandHandler(JpaMembershipTypeRepository jpaMembershipTypeRepository) {
        this.jpaMembershipTypeRepository = jpaMembershipTypeRepository;
    }
    
    @Override
    @Transactional
    public CommandResult handle(DeleteMemberByIdCommand command) {
        var membershipType = jpaMembershipTypeRepository.findById(command.getMembershipTypeId().getValue())
                .orElseThrow(() -> new NotFoundException("Membership type not found"));

        jpaMembershipTypeRepository.delete(membershipType);

        return CommandResult.success(membershipType.getId());
    }
}