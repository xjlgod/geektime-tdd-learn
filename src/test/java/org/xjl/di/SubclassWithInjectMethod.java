package org.xjl.di;

import jakarta.inject.Inject;

public class SubclassWithInjectMethod extends SuperClassWithInjectMethod {
    int subCalled = 0;

    @Inject
    void installAnother() {
        subCalled = superCalled + 1;
    }
}
