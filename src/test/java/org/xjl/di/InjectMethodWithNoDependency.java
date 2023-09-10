package org.xjl.di;

import jakarta.inject.Inject;

public class InjectMethodWithNoDependency {
    boolean called = false;
    @Inject
    public void install() {
        this.called = true;
    }
}
