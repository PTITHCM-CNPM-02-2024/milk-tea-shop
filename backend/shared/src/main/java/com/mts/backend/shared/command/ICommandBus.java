package com.mts.backend.shared.command;

public interface ICommandBus{
    <R, C extends ICommand<R>> void register(Class<C> commandClass, ICommandHandler<C, R> handler);
    <R, C extends ICommand<R>> R dispatch(C command);
}
