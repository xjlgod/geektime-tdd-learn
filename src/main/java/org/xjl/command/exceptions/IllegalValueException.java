package org.xjl.command.exceptions;

public class IllegalValueException extends RuntimeException{
    private final String value;
    private final String option;

    public String getOption() {
        return option;
    }

    public IllegalValueException(String value, String option) {
        this.value = value;
        this.option = option;
    }

    public String getValue() {
        return value;
    }
}
