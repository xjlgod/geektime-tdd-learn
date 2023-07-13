package org.xjl.command.exceptions;

public class InsufficientArgumentsException extends RuntimeException {
    private final String option;

    public InsufficientArgumentsException(String option) {
        super(option);
        this.option = option;
    }

    public String getOption() {
        return option;
    }
}
