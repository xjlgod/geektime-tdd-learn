package org.xjl.di;

import jakarta.inject.Inject;

public class SuperClassWithInjectMethod {
    int superCalled = 0;

    @Inject
    void install() {
        superCalled++;
    }
}
