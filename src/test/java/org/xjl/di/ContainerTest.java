package org.xjl.di;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static junit.framework.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ContainerTest {
    private Context context;

    @BeforeEach
    public void setup() {
        context = new Context();
    }

    @Nested
    public class ComponentConstruction {
        @Test
        public void should_bind_type_to_a_specific_instance() {
            Component instance = new Component(){};
            context.bind(Component.class, instance);
            assertSame(instance, context.get(Component.class).orElseThrow(DependencyNotFoundException::new));
        }

        //TODO: abstract class
        //TODO: interface

        // component dose not exist
        @Test
        public void should_bind_a_type_to_a_specific_instance() {
            Optional<Component> component = context.get(Component.class);
            assertFalse(component.isPresent());
        }

        @Nested
        public class ConstructorInjection {
            //TODO: No args constructor
            @Test
            public void should_bind_type_to_a_class_with_default_constructor() {
                context.bind(Component.class, ComponentWithDefaultConstructor.class);
                Component component = context.get(Component.class).get();
                assertNotNull(component);
                assertTrue(component instanceof ComponentWithDefaultConstructor);
            }

            //With injection
            @Test
            public void should_bind_a_type_to_a_class_with_inject_constructor() {
                Dependency dependency = new Dependency() {};
                context.bind(Component.class, ComponentWithInjectionConstructor.class);
                context.bind(Dependency.class, dependency);

                Component instance = context.get(Component.class).get();
                assertNotNull(instance);
                assertEquals(dependency, ((ComponentWithInjectionConstructor) instance).getDependcy());
            }

            // A -> B -> C
            @Test
            public void should_bind_type_to_a_class_with_transitive_dependencies() {
                context.bind(Component.class, ComponentWithInjectionConstructor.class);
                context.bind(Dependency.class, DependencyWithInjectConstructor.class);
                context.bind(String.class, "test");

                Component instance = context.get(Component.class).get();
                assertNotNull(instance);

                Dependency dependency = ((ComponentWithInjectionConstructor) instance).getDependcy();
                assertNotNull(dependency);
                assertEquals("test", ((DependencyWithInjectConstructor) dependency).getStr());
            }

            // multi inject constructors
            @Test
            public void should_throw_exception_if_multi_inject_constructors_provided() {
                assertThrows(IllegalComponentException.class, () -> {
                    context.bind(Component.class, ComponentWithMultiInjectConstructors.class);
                });
            }

            // no default constructor and inject constructor
            @Test
            public void should_throw_exception_if_no_default_constructor_nor_inject_constructors_provided() {
                assertThrows(IllegalComponentException.class, () -> {
                    context.bind(Component.class, ComponentWithNoDefaultConstructorNorInjectConstructors.class);
                });
            }

            // dependencies not exist
            @Test
            public void should_throw_exception_if_dependency_not_exist() {
                assertThrows(DependencyNotFoundException.class, () -> {
                    context.bind(Component.class, ComponentWithInjectionConstructor.class);
                    context.get(Component.class);
                });
            }

            // cyclic denpendcies
            @Test
            public void should_throw_exception_if_cyclic_dependencies_found() {
                context.bind(Component.class, ComponentWithInjectionConstructor.class);
                context.bind(Dependency.class, DependencyDependedOnComponent.class);
                assertThrows(CyclicDependenciesFound.class, () -> context.get(Component.class));
            }
        }
    }
}
