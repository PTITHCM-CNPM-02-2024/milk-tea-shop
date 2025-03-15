package com.mts.backend.shared.query;

/**
 * Interface QueryHandler
 * @param <Q> used for query
 * @param <R> used for result
 */
public interface IQueryHandler<Q extends IQuery<R>, R> {
    /**
     * Execute a query and return a result
     * @param query
     * @return R
     */
    R handle(Q query);
}
