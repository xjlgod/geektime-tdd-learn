package org.xjl.command;

import java.lang.annotation.Annotation;

public class BooleanOption{
    private boolean logging;

    public BooleanOption(@Option("l") boolean logging) {
        this.logging = logging;
    }

    public boolean getLogging() {
        return logging;
    }
}
