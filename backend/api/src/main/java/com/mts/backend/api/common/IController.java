package com.mts.backend.api.common;

import com.mts.backend.shared.command.CommandResult;
import com.mts.backend.shared.exception.DomainBusinessLogicException;
import com.mts.backend.shared.exception.DuplicateException;
import com.mts.backend.shared.exception.NotFoundException;
import org.springframework.http.ResponseEntity;

public interface IController {

    default <T> ResponseEntity<T> handleError(CommandResult commandResult) {
        if (commandResult != null && !commandResult.getFailureReasons().isEmpty()) {
            switch (commandResult.getFailureType()) {
                case DUPLICATE -> {
                    throw new DuplicateException(commandResult.getErrorMessage());
                }
                case NOT_FOUND -> {
                    throw new NotFoundException(commandResult.getErrorMessage());
                }
                case BUSINESS_RULE -> {
                    throw new DomainBusinessLogicException(commandResult.getFailureReasons());
                }
                default -> {
                    throw new RuntimeException(commandResult.getErrorMessage());
                }
            }
        }
        return ResponseEntity.badRequest().build();
    }
}
