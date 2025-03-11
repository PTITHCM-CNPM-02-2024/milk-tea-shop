package com.mts.backend.infrastructure.persistence;

import org.springframework.data.domain.Persistable;

import java.time.temporal.Temporal;
import java.util.Optional;

public interface IAuditableEntity<U, ID, T extends Temporal> extends Persistable<ID>{
    /**
     * Returns the user who created this entity.
     *
     * @return the createdBy
     */
    Optional<U> getCreatedBy();

    /**
     * Sets the user who created this entity.
     *
     * @param createdBy the creating entity to set
     */
    void setCreatedBy(U createdBy);

    /**
     * Returns the creation date of the entity.
     *
     * @return the createdAt
     */
    Optional<T> getCreatedAt();

    /**
     * Sets the creation date of the entity.
     *
     * @param createdAt the creation date to set
     */
    void setCreatedAt(T createdAt);

    /**
     * Returns the user who modified the entity lastly.
     *
     * @return the lastModifiedBy
     */
    Optional<U> getUpdatedBy();

    /**
     * Sets the user who modified the entity lastly.
     *
     * @param updatedBy the last modifying entity to set
     */
    void setUpdatedBy(U updatedBy);

    /**
     * Returns the date of the last modification.
     *
     * @return the updatedAt
     */
    Optional<T> getUpdatedAt();

    /**
     * Sets the date of the last modification.
     *
     * @param updatedAt the date of the last modification to set
     */
    void setUpdatedAt(T updatedAt);

}