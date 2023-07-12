package org.xjl.command;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.annotation.Annotation;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SingleValueOptionParserTest {
    @Test// sad path
    public void should_not_accept_extra_argument_for_single_valued_option() {
        TooManyArgumentsException e = assertThrows(TooManyArgumentsException.class, () -> {
           new SingleValueOptionParser<>(Integer::parseInt, 0).parse(asList("p", "8080", "8081"), option("p"));
        });
        assertEquals("p", e.getOption());
    }

    @ParameterizedTest// sad path
    @ValueSource(strings = {"-p -l", "-p"})
    void should_not_accept_insufficient_argument_for_single_valued_option(String arguments) {
        InsufficientArgumentsException e = assertThrows(InsufficientArgumentsException.class, () -> {
            new SingleValueOptionParser<>(Integer::parseInt, 0).parse(asList(arguments.split(" ")), option("p"));
        });
        assertEquals("p", e.getOption());
    }

    @Test// default value
    void should_set_default_value_to_0_for_int_option() {
        Function<String, Object> whatever = (it) -> null;
        Object defaultValue = new Object();
        assertEquals(defaultValue, new SingleValueOptionParser<>(whatever, defaultValue).parse(asList(), option("p")));
    }



    @Test// sad path
    public void should_not_accept_extra_argument_for_string_single_valued_option() {
        TooManyArgumentsException e = assertThrows(TooManyArgumentsException.class, () -> {
            new SingleValueOptionParser<>(String::valueOf, "").parse(asList("-d", "/usr/logs", "/usr/vars"), option("d"));
        });
        assertEquals("d", e.getOption());
    }

    @Test// happy path
    void should_parse_int_as_option_value() {
        assertEquals(new Integer(8080), new SingleValueOptionParser<>(Integer::parseInt, 0).parse(asList("-p", "8080"), option("p")));
    }


    @Test// happy path, not modify to optionParser
    void should_parse_string_as_option_value() {
        assertEquals("/usr/logs",  new SingleValueOptionParser<>(String::valueOf, "").parse(asList("-d", "/usr/logs"), option("d")));
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
