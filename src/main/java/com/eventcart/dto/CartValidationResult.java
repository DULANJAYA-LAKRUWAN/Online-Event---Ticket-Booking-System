package com.eventcart.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Result of validating the shopping cart against active database state.
 */
public class CartValidationResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean valid = true;
    private final List<String> errorMessages = new ArrayList<>();
    private final List<String> warningMessages = new ArrayList<>();

    public CartValidationResult() {
    }

    public void addError(String message) {
        this.valid = false;
        this.errorMessages.add(message);
    }

    public void addWarning(String message) {
        this.warningMessages.add(message);
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public List<String> getWarningMessages() {
        return warningMessages;
    }
}
