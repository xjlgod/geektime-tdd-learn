package org.xjl.di;

import java.util.Optional;

public interface Context {
    <Type> Optional<Type> get(Class<Type> typeClass);
}
