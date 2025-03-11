package com.mts.backend.shared.command;

/**
 * Interface ICommandHandler
 * @param <C> used for command
 * @param <R> used for result
 */
public interface ICommandHandler< C extends ICommand<R>, R > {
    /**
     * Execute a command and return a result
     * @param command
     * @return R
     */
    R handle(C command);
}
