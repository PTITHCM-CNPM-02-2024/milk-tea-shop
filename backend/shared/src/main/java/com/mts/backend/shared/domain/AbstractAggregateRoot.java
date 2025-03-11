package com.mts.backend.shared.domain;


public abstract class AbstractAggregateRoot<ID extends Identifiable> extends AbstractEntity<ID> {
    

    /**
     * @param id 
     */
    protected AbstractAggregateRoot(ID id) {
        super(id);
    }

    /**
     * @return 
     */
    @Override
    public ID getId() {
        return super.getId();
    }

    /**
     * @return 
     */
    @Override
    public boolean isNew() {
        return super.isNew();
    }
    

}
