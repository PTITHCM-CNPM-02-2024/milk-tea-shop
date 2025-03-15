package com.mts.backend.shared.command;



import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


public class AbstractCommandBus implements ICommandBus{
    
    private final Map<Class<?>, ICommandHandler<?, ?>> handlers = new HashMap<>();


    /**
     * @param commandClass 
     * @param handler
     * @param <R>
     * @param <C>
     */
    @Override
    public <R, C extends ICommand<R>> void register(Class<C> commandClass, ICommandHandler<C, R> handler) {
        handlers.put(commandClass, handler);
    }

    /**
     * @param command 
     * @param <R>
     * @param <C>
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public <R, C extends ICommand<R>> R dispatch(C command) {
        ICommandHandler<C, R> handler = (ICommandHandler<C, R>) handlers.get(command.getClass());
        
        if (handler == null) {
            throw new IllegalArgumentException("No handler for " + command.getClass());
        }
        
        return handler.handle(command);
    }
}
