package org.xjl.di;

import jakarta.inject.Inject;

public class SubClassOverrideSuperClassWithNoInject extends SuperClassWithInjectMethod {
    void install() {
        super.install();
    }
}
