package com.mts.backend.shared.query;


import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AbstractQueryBus implements IQueryBus {
    private final Map<Class<?>, IQueryHandler<?, ?>> handlers = new HashMap<>();


    /**
     * @param query 
     * @param <R>
     * @param <Q>
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public <R, Q extends IQuery<R>> R dispatch(Q query) {
        IQueryHandler<Q, R> handler = (IQueryHandler<Q, R>) handlers.get(query.getClass());
        
        if (handler == null) {
            throw new IllegalArgumentException("No handler for " + query.getClass());
        }
        
        return handler.handle(query);
    }

    /**
     * @param queryClass 
     * @param handler
     * @param <R>
     * @param <Q>
     */
    @Override
    public <R, Q extends IQuery<R>> void register(Class<Q> queryClass, IQueryHandler<Q, R> handler) {
        handlers.put(queryClass, handler);
    }
}
