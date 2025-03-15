package com.mts.backend.shared.domain;

import com.mts.backend.shared.value_object.IValueObject;
import com.mts.backend.shared.value_object.ValueObjectValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractEntity<ID extends Identifiable> implements IEntity<ID>{
    private final ID id;

    protected List<String> businessErrors = new ArrayList<>();


    protected AbstractEntity(ID id) {
        this.id = Objects.requireNonNull(id, "ID không được null");
    }

    @Override
    public ID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return getId() == null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        AbstractEntity<?> that = (AbstractEntity<?>) o;
        
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public IValueObject checkAndAssign(ValueObjectValidationResult validateValueObject) {
        if (validateValueObject.getValueObject() != null){
            return validateValueObject.getValueObject();
        }else {
            if (!validateValueObject.getBusinessErrors().isEmpty()){
                this.businessErrors.addAll(validateValueObject.getBusinessErrors());
            }
            return null;
        }
    }
}
