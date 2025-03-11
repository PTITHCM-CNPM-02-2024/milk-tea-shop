package com.mts.backend.shared.exception;

import java.util.List;

public class DomainBusinessLogicException extends DomainException {
    public DomainBusinessLogicException(List<String> businessErrors) {
        super(String.join(", ", businessErrors));
    }
}
