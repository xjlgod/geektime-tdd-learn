package org.xjl.command;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

public class BooleanOptionParserTest {
    @Test// sad  path
    public void should_not_accept_extra_argument_for_boolean_option() {
        TooManyArgumentsException e = assertThrows(TooManyArgumentsException.class,
                () ->  new BooleanOptionParser().parse(asList("-l", "t"), option("l")));
        assertEquals("l", e.getOption());
    }

    @Test// happy path
    void should_set_boolean_option_to_return_true_if_option_present(){
        assertTrue((Boolean) new BooleanOptionParser().parse(asList("-l"), option("l")));
    }

    @Test// default value
    void should_set_boolean_option_to_return_false_if_option_not_present(){
        assertFalse((Boolean) new BooleanOptionParser().parse(asList(), option("l")));
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