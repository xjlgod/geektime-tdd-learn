package org.xjl.di;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;

import java.util.Optional;
import java.util.Set;

import static junit.framework.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ContainerTest {
    private ContextConfig contextConfig;

    @BeforeEach
    public void setup() {
        contextConfig = new ContextConfig();
    }

    @Nested
    public class ComponentConstruction {
        @Test
        public void should_bind_type_to_a_specific_instance() {
            Component instance = new Component(){};
            contextConfig.bind(Component.class, instance);
            assertSame(instance, contextConfig.getContext().get(Component.class).get());
        }

        //TODO: abstract class
        //TODO: interface

        // component dose not exist
        @Test
        public void should_bind_a_type_to_a_specific_instance() {
            Optional<Component> component = contextConfig.getContext().get(Component.class);
            assertFalse(component.isPresent());
        }

        @Nested
        public class ConstructorInjection {
            //TODO: No args constructor
            @Test
            public void should_bind_type_to_a_class_with_default_constructor() {
                contextConfig.bind(Component.class, ComponentWithDefaultConstructor.class);
                Component component = contextConfig.getContext().get(Component.class).get();
                assertNotNull(component);
                assertTrue(component instanceof ComponentWithDefaultConstructor);
            }

            //With injection
            @Test
            public void should_bind_a_type_to_a_class_with_inject_constructor() {
                Dependency dependency = new Dependency() {};
                contextConfig.bind(Component.class, ComponentWithInjectionConstructor.class);
                contextConfig.bind(Dependency.class, dependency);

                Component instance = contextConfig.getContext().get(Component.class).get();
                assertNotNull(instance);
                assertEquals(dependency, ((ComponentWithInjectionConstructor) instance).getDependcy());
            }

            // A -> B -> C
            @Test
            public void should_bind_type_to_a_class_with_transitive_dependencies() {
                contextConfig.bind(Component.class, ComponentWithInjectionConstructor.class);
                contextConfig.bind(Dependency.class, DependencyWithInjectConstructor.class);
                contextConfig.bind(String.class, "test");

                Component instance = contextConfig.getContext().get(Component.class).get();
                assertNotNull(instance);

                Dependency dependency = ((ComponentWithInjectionConstructor) instance).getDependcy();
                assertNotNull(dependency);
                assertEquals("test", ((DependencyWithInjectConstructor) dependency).getStr());
            }

            // multi inject constructors
            @Test
            public void should_throw_exception_if_multi_inject_constructors_provided() {
                assertThrows(IllegalComponentExceptionException.class, () -> {
                    contextConfig.bind(Component.class, ComponentWithMultiInjectConstructors.class);
                });
            }

            // no default constructor and inject constructor
            @Test
            public void should_throw_exception_if_no_default_constructor_nor_inject_constructors_provided() {
                assertThrows(IllegalComponentExceptionException.class, () -> {
                    contextConfig.bind(Component.class, ComponentWithNoDefaultConstructorNorInjectConstructors.class);
                });
            }

            // dependencies not exist
            @Test
            public void should_throw_exception_if_dependency_not_exist() {
                DependencyNotFoundException dependencyNotFoundException = assertThrows(DependencyNotFoundException.class, () -> {
                    contextConfig.bind(Component.class, ComponentWithInjectionConstructor.class);
                    contextConfig.getContext();
                });
                assertEquals(Dependency.class, dependencyNotFoundException.getDependency());
                assertEquals(Component.class, dependencyNotFoundException.getComponent());
            }

            // cyclic denpendcies
            @Test
            public void should_throw_exception_if_cyclic_dependencies_found() {
                contextConfig.bind(Component.class, ComponentWithInjectionConstructor.class);
                contextConfig.bind(Dependency.class, DependencyDependedOnComponent.class);
                CyclicDependenciesFoundException cyclicDependenciesFoundException = assertThrows(CyclicDependenciesFoundException.class, () -> contextConfig.getContext());

                Set<Class<?>> classes = Sets.newSet(cyclicDependenciesFoundException.getComponents());

                assertEquals(2, classes.size());
                assertTrue(classes.contains(Component.class));
                assertTrue(classes.contains(Dependency.class));

            }

            @Test
            public void should_throw_exception_if_transitive_cyclic_dependencies_found() {
                contextConfig.bind(Component.class, ComponentWithInjectionConstructor.class);
                contextConfig.bind(Dependency.class, DependencyDependedOnAnotherDependency.class);
                contextConfig.bind(AnotherDependency.class, AnotherDependencyDependedOnComponent.class);
                CyclicDependenciesFoundException cyclicDependenciesFoundException = assertThrows(CyclicDependenciesFoundException.class, () -> contextConfig.getContext());

                Set<Class<?>> classes = Sets.newSet(cyclicDependenciesFoundException.getComponents());

                assertEquals(3, classes.size());
                assertTrue(classes.contains(Component.class));
                assertTrue(classes.contains(Dependency.class));
                assertTrue(classes.contains(AnotherDependency.class));
            }
        }

        @Nested
        public class FieldInjection {
            // TDD inject field
            @Test
            public void should_inject_dependency_via_field() {
                Dependency dependency = new Dependency(){};
                contextConfig.bind(Dependency.class, dependency);
                contextConfig.bind(ComponentWithFieldInjection.class, ComponentWithFieldInjection.class);
                ComponentWithFieldInjection component = contextConfig.getContext().get(ComponentWithFieldInjection.class).get();
                Assertions.assertSame(component.dependency, dependency);
            }

            @Test
            public void should_inject_dependency_via_superclass_inject_field() {
                Dependency dependency = new Dependency(){};
                contextConfig.bind(Dependency.class, dependency);
                contextConfig.bind(SubclassWithFieldInjection.class, SubclassWithFieldInjection.class);
                SubclassWithFieldInjection component = contextConfig.getContext().get(SubclassWithFieldInjection.class).get();
                Assertions.assertSame(component.dependency, dependency);
            }

//            public void should_create_component_with_injection_field() {
//                Context context = Mockito.mock(Context.class);
//                Dependency dependency = Mockito.mock(Dependency.class);
//                Mockito.when(context.get(Mockito.eq(Dependency.class))).thenReturn(Optional.ofNullable(dependency));
//                ConstructorInjectionProvider<ComponentWithFieldInjection> provider = new ConstructorInjectionProvider<>(ComponentWithFieldInjection.class);
//                ComponentWithFieldInjection component = provider.get(context);
//                Assertions.assertSame(component.dependency, dependency);
//            }

            // TDD throw exception if field is final

            // TDD provide dependency information for field injection
            @Test
            public void should_throw_exception_when_field_dependency_missing() {
                contextConfig.bind(ComponentWithFieldInjection.class, ComponentWithFieldInjection.class);
                assertThrows(DependencyNotFoundException.class, () -> contextConfig.getContext());
            }

            @Test
            public void should_include_field_dependency_in_dependencies() {
                ConstructorInjectionProvider<ComponentWithFieldInjection> provider = new ConstructorInjectionProvider<>(ComponentWithFieldInjection.class);
                Assertions.assertArrayEquals(new Class<?>[]{Dependency.class}, provider.getDependencies().toArray(new Class<?>[]{}));
            }
//
//            class DependencyWithFieldInjection implements Dependency {
//                @Inject
//                ComponentWithFieldInjection component;
//            }
//
//            @Test
//            public void should_throw_exception_when_filed_has_cyclic_dependencies() {
//                contextConfig.bind(ComponentWithFieldInjection.class, ComponentWithFieldInjection.class);
//                contextConfig.bind(Dependency.class, DependencyWithFieldInjection.class);
//
//                assertThrows(CyclicDependenciesFoundException.class, () -> contextConfig.getContext());
//            }
        }

    }
}
