package org.xjl.di;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static junit.framework.Assert.*;

public class ContainerTest {

    @Nested
    public class ComponentConstruction {

        @Test
        public void should_bind_type_to_a_specific_instance() {
            Context context = new Context();
            Component instance = new Component();
            context.bind(Component.class, instance);
            assertSame(instance, context.get(Component.class));
        }

        //TODO: abstract class
        //TODO: interface

        @Nested
        public class ConstructorInjection {
            //TODO: No args constructor
            @Test
            public void should_bind_type_to_a_class_with_default_constructor() {
                Context context = new Context();
                context.bind(Component.class, ComponentWithDefaultConstructor.class);
                Component component = context.get(Component.class);
                assertNotNull(component);
                assertTrue(component instanceof ComponentWithDefaultConstructor);
            }
        }
    }
}
