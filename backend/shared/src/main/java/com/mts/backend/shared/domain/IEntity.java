package com.mts.backend.shared.domain;

public interface IEntity <ID extends Identifiable> {
    ID getId();
    boolean isNew();
}
