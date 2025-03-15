package com.mts.backend.shared.query;



public interface IQueryBus {
    <R, Q extends IQuery<R>> R dispatch(Q query);
    <R, Q extends IQuery<R>> void register(Class<Q> queryClass, IQueryHandler<Q, R> handler);
}
