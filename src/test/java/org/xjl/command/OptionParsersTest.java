package org.xjl.command;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.xjl.command.exceptions.IllegalValueException;
import org.xjl.command.exceptions.InsufficientArgumentsException;
import org.xjl.command.exceptions.TooManyArgumentsException;

import java.lang.annotation.Annotation;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

public class OptionParsersTest {
    @Nested
    class UnaryOptionParserTest {
        @Test// sad path
        public void should_not_accept_extra_argument_for_single_valued_option() {
            TooManyArgumentsException e = assertThrows(TooManyArgumentsException.class, () -> {
                OptionParsers.unary(Integer::parseInt, 0).parse(asList("-p", "8080", "8081"), option("p"));
            });
            assertEquals("p", e.getOption());
        }

        @ParameterizedTest// sad path
        @ValueSource(strings = {"-p -l", "-p"})
        void should_not_accept_insufficient_argument_for_single_valued_option(String arguments) {
            InsufficientArgumentsException e = assertThrows(InsufficientArgumentsException.class, () -> {
                OptionParsers.unary(Integer::parseInt, 0).parse(asList(arguments.split(" ")), option("p"));
            });
            assertEquals("p", e.getOption());
        }

        @Test// default value
        void should_set_default_value_to_0_for_int_option() {
            Function<String, Object> whatever = (it) -> null;
            Object defaultValue = new Object();
            assertEquals(defaultValue, OptionParsers.unary(whatever, defaultValue).parse(asList(), option("p")));
        }

        @Test// sad path
        public void should_not_accept_extra_argument_for_string_single_valued_option() {
            TooManyArgumentsException e = assertThrows(TooManyArgumentsException.class, () -> {
                OptionParsers.unary(String::valueOf, "").parse(asList("-d", "/usr/logs", "/usr/vars"), option("d"));
            });
            assertEquals("d", e.getOption());
        }

        @Test// happy path
        void should_parse_int_as_option_value() {
            assertEquals(new Integer(8080), OptionParsers.unary(Integer::parseInt, 0).parse(asList("-p", "8080"), option("p")));
        }


        @Test// happy path, not modify to optionParser
        void should_parse_string_as_option_value() {
            assertEquals("/usr/logs",  OptionParsers.unary(String::valueOf, "").parse(asList("-d", "/usr/logs"), option("d")));
        }
    }

    @Nested
    class BooleanOptionParserTest {
        @Test// sad  path
        public void should_not_accept_extra_argument_for_boolean_option() {
            TooManyArgumentsException e = assertThrows(TooManyArgumentsException.class,
                    () ->  OptionParsers.bool().parse(asList("-l", "t"), option("l")));
            Assertions.assertEquals("l", e.getOption());
        }

        @Test// happy path
        void should_set_boolean_option_to_return_true_if_option_present(){
            assertTrue((Boolean) OptionParsers.bool().parse(asList("-l"), option("l")));
        }

        @Test// default value
        void should_set_boolean_option_to_return_false_if_option_not_present(){
            assertFalse((Boolean) OptionParsers.bool().parse(asList(), option("l")));
        }
    }

    @Nested
    class ListOptionParserTest{
        //TODO: -g "this" "is" {"this", is"}
        @Test
        public void should_parse_list_value() {
            assertArrayEquals(new String[]{"this", "is"}, OptionParsers.list(String[]::new, String::valueOf).
                    parse(asList("-g", "this", "is"), option("g")));
        }

        //TODO: default value []
        @Test
        public void should_use_empty_array_as_default_value() {
            String[] values = OptionParsers.list(String[]::new, String::valueOf).parse(asList(), option("g"));
            assertEquals(0, values.length);
        }
        //TODO: -d a throw exception
        @Test
        public void should_throw_exception_if_value_can_not_parse_value() {
            Function<String, String> parser = (it) -> {throw new RuntimeException();};
            IllegalValueException e = assertThrows(IllegalValueException.class, () -> OptionParsers.list(String[]::new, parser).
                    parse(asList("-g", "this", "is"), option("g")));
            assertEquals("g", e.getOption());
            assertEquals("this", e.getValue());
        }

        @Test
        public void should_not_treat_negative_int_as_flag() {
            assertArrayEquals(new Integer[]{-1, -2}, OptionParsers.list(Integer[]::new, Integer::parseInt).
                    parse(asList("-d", "-1", "-2"), option("d")));
        }
    }

    static Option option(String value){
        return new Option(){

            @Override
            public Class<? extends Annotation> annotationType() {
                return Option.class;
            }

            @Override
            public String value() {
                return value;
            }
        };
    }
}
