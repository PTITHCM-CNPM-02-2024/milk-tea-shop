package com.mts.backend.infrastructure.persistence;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

@MappedSuperclass
public abstract class AbstractAuditableEntity<U, PK extends Serializable>  implements IAuditableEntity<U, PK, LocalDateTime> {
    @Transient
    private @Nullable U createdBy;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
    @Transient
    private @Nullable U updatedBy;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    @Override
    public Optional<U> getCreatedBy() {
        return Optional.ofNullable(createdBy);
    }

    @Override
    public void setCreatedBy(@Nullable U createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Optional<LocalDateTime> getCreatedAt() {
        return null == createdAt ? Optional.empty() : Optional.of(createdAt);
    }

    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public Optional<U> getUpdatedBy() {
        return Optional.ofNullable(updatedBy);
    }

    @Override
    public void setUpdatedBy(@Nullable U updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public Optional<LocalDateTime> getUpdatedAt() {
        return null == updatedAt ? Optional.empty() : Optional.of(updatedAt);
    }

    @Override
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}