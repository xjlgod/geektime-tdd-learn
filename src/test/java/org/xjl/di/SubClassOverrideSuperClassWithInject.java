package org.xjl.di;

import jakarta.inject.Inject;

public class SubClassOverrideSuperClassWithInject extends SuperClassWithInjectMethod{
    @Inject
    void install() {
        super.install();
    }
}
