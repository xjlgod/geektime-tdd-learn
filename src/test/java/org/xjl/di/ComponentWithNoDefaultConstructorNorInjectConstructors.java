package org.xjl.di;

import jakarta.inject.Inject;

public class ComponentWithNoDefaultConstructorNorInjectConstructors implements Component {
    public ComponentWithNoDefaultConstructorNorInjectConstructors(String name, double value) {
    }
}
