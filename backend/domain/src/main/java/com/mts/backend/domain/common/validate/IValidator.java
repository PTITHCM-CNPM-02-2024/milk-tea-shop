package com.mts.backend.domain.common.validate;

public interface IValidator<T> {
    
    public void validate(T t);
}
