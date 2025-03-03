package com.ptithcm.infrastructure.persistence;

import jakarta.persistence.*;
import org.springframework.lang.Nullable;

import java.io.Serializable;

@MappedSuperclass
@AttributeOverrides(
        {
                @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")),
                @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"))
        }
)
public abstract class BaseEntity<PK extends Serializable> extends AbstractAuditableEntity<String, PK> {

        /**
         * Returns if the {@code Persistable} is new or was persisted already.
         *
         * @return if {@literal true} the object is new.
         */
        @Override
        @Transient
        public boolean isNew() {
                return null == getId();
        }
}
