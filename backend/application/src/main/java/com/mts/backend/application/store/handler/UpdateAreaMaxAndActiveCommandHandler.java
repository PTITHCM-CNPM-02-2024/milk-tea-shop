package com.mts.backend.application.store.handler;

import com.mts.backend.application.store.command.UpdateAreaMaxAndActiveCommand;
import com.mts.backend.application.store.event.ChangeAreaMaxAndActiveEvent;
import com.mts.backend.domain.store.AreaEntity;
import com.mts.backend.domain.store.identifier.AreaId;
import com.mts.backend.domain.store.jpa.JpaAreaRepository;
import com.mts.backend.domain.store.repository.IServiceTableRepository;
import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.command.ICommandHandler;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UpdateAreaMaxAndActiveCommandHandler implements ICommandHandler<UpdateAreaMaxAndActiveCommand, CommandResult> {

    private final JpaAreaRepository areaRepository;

    private final ApplicationEventPublisher eventPublisher;

    public UpdateAreaMaxAndActiveCommandHandler(JpaAreaRepository areaRepository, IServiceTableRepository serviceTableRepository,
                                                ApplicationEventPublisher eventPublisher) {
        this.areaRepository = areaRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CommandResult handle(UpdateAreaMaxAndActiveCommand command) {
        Objects.requireNonNull(command, "Update area max table command is required");

        var area = mustExistArea(command.getAreaId());

        var maxTable = command.getMaxTable().orElse(null);
        
        area.changeMaxTable(maxTable);
        area.changeActive(command.isActive());
        
        var event = new ChangeAreaMaxAndActiveEvent(this,
                ChangeAreaMaxAndActiveEvent.Data.builder().maxTable(maxTable).active(area.getActive()).id(area.getId()).build());

        eventPublisher.publishEvent(event);


        var savedArea = areaRepository.save(area);

        return CommandResult.success(savedArea.getId().getValue());
    }

    private AreaEntity mustExistArea(AreaId areaId) {
        return areaRepository.findById(areaId)
                .orElseThrow(() -> new NotFoundException("Khu vực không tồn tại"));
    }

    // TODO: Implement verifyMaxTable method


}
