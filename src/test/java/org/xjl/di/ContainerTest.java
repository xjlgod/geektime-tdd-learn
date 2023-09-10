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
    private ContextConfig config;

    @BeforeEach
    public void setup() {
        config = new ContextConfig();
    }

    @Nested
    public class ComponentConstruction {
        @Test
        public void should_bind_type_to_a_specific_instance() {
            Component instance = new Component(){};
            config.bind(Component.class, instance);
            assertSame(instance, config.getContext().get(Component.class).get());
        }

        //TODO: abstract class
        //TODO: interface

        // component dose not exist
        @Test
        public void should_bind_a_type_to_a_specific_instance() {
            Optional<Component> component = config.getContext().get(Component.class);
            assertFalse(component.isPresent());
        }

        @Nested
        public class ConstructorInjection {
            //TODO: No args constructor
            @Test
            public void should_bind_type_to_a_class_with_default_constructor() {
                config.bind(Component.class, ComponentWithDefaultConstructor.class);
                Component component = config.getContext().get(Component.class).get();
                assertNotNull(component);
                assertTrue(component instanceof ComponentWithDefaultConstructor);
            }

            //With injection
            @Test
            public void should_bind_a_type_to_a_class_with_inject_constructor() {
                Dependency dependency = new Dependency() {};
                config.bind(Component.class, ComponentWithInjectionConstructor.class);
                config.bind(Dependency.class, dependency);

                Component instance = config.getContext().get(Component.class).get();
                assertNotNull(instance);
                assertEquals(dependency, ((ComponentWithInjectionConstructor) instance).getDependcy());
            }

            // A -> B -> C
            @Test
            public void should_bind_type_to_a_class_with_transitive_dependencies() {
                config.bind(Component.class, ComponentWithInjectionConstructor.class);
                config.bind(Dependency.class, DependencyWithInjectConstructor.class);
                config.bind(String.class, "test");

                Component instance = config.getContext().get(Component.class).get();
                assertNotNull(instance);

                Dependency dependency = ((ComponentWithInjectionConstructor) instance).getDependcy();
                assertNotNull(dependency);
                assertEquals("test", ((DependencyWithInjectConstructor) dependency).getStr());
            }

            // multi inject constructors
            @Test
            public void should_throw_exception_if_multi_inject_constructors_provided() {
                assertThrows(IllegalComponentExceptionException.class, () -> {
                    config.bind(Component.class, ComponentWithMultiInjectConstructors.class);
                });
            }

            // no default constructor and inject constructor
            @Test
            public void should_throw_exception_if_no_default_constructor_nor_inject_constructors_provided() {
                assertThrows(IllegalComponentExceptionException.class, () -> {
                    config.bind(Component.class, ComponentWithNoDefaultConstructorNorInjectConstructors.class);
                });
            }

            // dependencies not exist
            @Test
            public void should_throw_exception_if_dependency_not_exist() {
                DependencyNotFoundException dependencyNotFoundException = assertThrows(DependencyNotFoundException.class, () -> {
                    config.bind(Component.class, ComponentWithInjectionConstructor.class);
                    config.getContext();
                });
                assertEquals(Dependency.class, dependencyNotFoundException.getDependency());
                assertEquals(Component.class, dependencyNotFoundException.getComponent());
            }

            // cyclic denpendcies
            @Test
            public void should_throw_exception_if_cyclic_dependencies_found() {
                config.bind(Component.class, ComponentWithInjectionConstructor.class);
                config.bind(Dependency.class, DependencyDependedOnComponent.class);
                CyclicDependenciesFoundException cyclicDependenciesFoundException = assertThrows(CyclicDependenciesFoundException.class, () -> config.getContext());

                Set<Class<?>> classes = Sets.newSet(cyclicDependenciesFoundException.getComponents());

                assertEquals(2, classes.size());
                assertTrue(classes.contains(Component.class));
                assertTrue(classes.contains(Dependency.class));

            }

            @Test
            public void should_throw_exception_if_transitive_cyclic_dependencies_found() {
                config.bind(Component.class, ComponentWithInjectionConstructor.class);
                config.bind(Dependency.class, DependencyDependedOnAnotherDependency.class);
                config.bind(AnotherDependency.class, AnotherDependencyDependedOnComponent.class);
                CyclicDependenciesFoundException cyclicDependenciesFoundException = assertThrows(CyclicDependenciesFoundException.class, () -> config.getContext());

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
                config.bind(Dependency.class, dependency);
                config.bind(ComponentWithFieldInjection.class, ComponentWithFieldInjection.class);
                ComponentWithFieldInjection component = config.getContext().get(ComponentWithFieldInjection.class).get();
                Assertions.assertSame(component.dependency, dependency);
            }

            @Test
            public void should_inject_dependency_via_superclass_inject_field() {
                Dependency dependency = new Dependency(){};
                config.bind(Dependency.class, dependency);
                config.bind(SubclassWithFieldInjection.class, SubclassWithFieldInjection.class);
                SubclassWithFieldInjection component = config.getContext().get(SubclassWithFieldInjection.class).get();
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
                config.bind(ComponentWithFieldInjection.class, ComponentWithFieldInjection.class);
                assertThrows(DependencyNotFoundException.class, () -> config.getContext());
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

        @Nested
        public class MethodInjection {
            // TODO inject method with no dependencies will be called
            @Test
            public void should_call_inject_method_even_if_no_dependency_declared() {
                config.bind(InjectMethodWithNoDependency.class, InjectMethodWithNoDependency.class);

                InjectMethodWithNoDependency component = config.getContext().get(InjectMethodWithNoDependency.class).get();
                assertTrue(component.called);
            }
            // TODO inject method with dependencies will be injected
            @Test
            public void should_inject_dependency_via_inject_method() {
                Dependency dependency = new Dependency() {};
                config.bind(Dependency.class, dependency);
                config.bind(InjectMethodWithDependency.class, InjectMethodWithDependency.class);

                InjectMethodWithDependency component = config.getContext().get(InjectMethodWithDependency.class).get();
                assertSame(dependency, component.dependency);
            }

            // TODO override inject method from superclass
            @Test
            public void should_inject_denpendencies_via_inject_method_from_superclass() {
                config.bind(SubclassWithInjectMethod.class, SubclassWithInjectMethod.class);

                SubclassWithInjectMethod component = config.getContext().get(SubclassWithInjectMethod.class).get();
                assertEquals(2, component.subCalled);
                assertEquals(1, component.superCalled);
            }

            @Test
            public void should_only_call_once_if_subclass_override_inject_method_with_inject() {
                config.bind(SubClassOverrideSuperClassWithInject.class, SubClassOverrideSuperClassWithInject.class);
                SubClassOverrideSuperClassWithInject component =config.getContext().get(SubClassOverrideSuperClassWithInject.class).get();

                assertEquals(1, component.superCalled);
            }

            @Test
            public void should_only_call_once_if_subclass_override_inject_method_with_no_inject() {
                config.bind(SubClassOverrideSuperClassWithNoInject.class, SubClassOverrideSuperClassWithNoInject.class);
                SubClassOverrideSuperClassWithNoInject component =config.getContext().get(SubClassOverrideSuperClassWithNoInject.class).get();

                assertEquals(0, component.superCalled);
            }


            // TODO include dependencies from inject methods
            @Test
            public void should_inclued_dependencies_from_inject_method() {
                ConstructorInjectionProvider<InjectMethodWithDependency> provider = new ConstructorInjectionProvider<>(InjectMethodWithDependency.class);
                Assertions.assertArrayEquals(new Class<?>[]{Dependency.class}, provider.getDependencies().toArray(new Class<?>[]{}));
            }
        }

    }
}
