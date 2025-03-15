package com.mts.backend.shared.mapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for bidirectional mapping between objects
 */
public abstract class AbstractBidirectionalMapper<S, T> implements IMapper<S, T> {
    /**
     * Maps a target object to a source object
     * @param target The target object
     * @return The mapped source object
     */
    public abstract S mapReverse(T target);
    
    /**
     * Maps a list of target objects to a list of source objects
     * @param targets The list of target objects
     * @return The list of mapped source objects
     */
    
    public List<S> mapAllReverse(List<T> targets) {
        if (targets == null) {
            return null;
        }

        List<S> sources = new ArrayList<>(targets.size());
        for (T target : targets) {
            sources.add(mapReverse(target));
        }
        return sources;
    }
}
