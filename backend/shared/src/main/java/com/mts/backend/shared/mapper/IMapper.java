package com.mts.backend.shared.mapper;

import java.util.ArrayList;
import java.util.List;

/**
*  Generic interface for mapping objects between types
 * @param <S> Source type
 * @param <T> Target type
 */
public interface IMapper<S, T> {
    /**
     * Maps a source object to a target object
     * @param source The source object
     * @return The mapped target object
     */
    T map(S source);

    /**
     * Maps a list of source objects to a list of target objects
     * @param sources The list of source objects
     * @return The list of mapped target objects
     */
    default List<T> mapAll(List<S> sources) {
        if (sources == null) {
            return null;
        }

        List<T> targets = new ArrayList<>(sources.size());
        for (S source : sources) {
            targets.add(map(source));
        }
        return targets;
    }
}
