package org.xjl.di;

import jakarta.inject.Inject;

public class DependencyWithInjectConstructor implements Dependency {
    private String str;

    @Inject
    public DependencyWithInjectConstructor(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }
}
