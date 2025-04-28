package com.mts.backend.application.account.handler;

import com.mts.backend.shared.exception.DomainException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.mts.backend.application.account.command.DeleteRoleByIdCommand;
import com.mts.backend.domain.account.jpa.JpaRoleRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;

@Service
public class DeleteRoleByIdCommandHandler implements ICommandHandler<DeleteRoleByIdCommand, CommandResult> {
    private final JpaRoleRepository jpaRoleRepository;

    public DeleteRoleByIdCommandHandler(JpaRoleRepository jpaRoleRepository) {
        this.jpaRoleRepository = jpaRoleRepository;
    }

    @Override
    public CommandResult handle(DeleteRoleByIdCommand command) {
        try{
            var role = jpaRoleRepository.findById(command.getRoleId().getValue())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy vai trò"));
            jpaRoleRepository.delete(role);
            return CommandResult.success(role.getId());
        }catch (DataIntegrityViolationException e){
            throw new DomainException("Vai trò này đang được sử dụng, hãy đảm bảo vai trò đang không được sử dụng bởi" +
                                      " tài khoản nào trước khi xóa");
        }
    }
    
}