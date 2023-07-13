package org.xjl.command;
import org.xjl.command.exceptions.IllegalOptionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Args {
    public static <T> T parse(Class<T> optionsClass, String... args) {
        try {
            List<String> arguments = Arrays.asList(args);
            Constructor<?> constructor =
                    optionsClass.getDeclaredConstructors()[0];
            Object[] values =
                    Arrays.stream(constructor.getParameters()).map(it ->
                            parseOption(arguments, it)).toArray();
            return (T) constructor.newInstance(values);
        } catch (IllegalOptionException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object parseOption(List<String> arguments, Parameter parameter) {
        if (!parameter.isAnnotationPresent(Option.class)) {
            throw new IllegalOptionException(parameter.getName());
        }
        return parserMap.get(parameter.getType()).parse(arguments, parameter.getAnnotation(Option.class));
    }

    private static Map<Class<?>, OptionParser> parserMap = new HashMap<Class<?>, OptionParser>(){{
        put(boolean.class, OptionParsers.bool());
        put(int.class, OptionParsers.unary(Integer::parseInt, 0));
        put(String.class, OptionParsers.unary(String::valueOf, ""));
        put(String[].class, OptionParsers.list(String[]::new, String::valueOf));
        put(Integer[].class, OptionParsers.list(Integer[]::new, Integer::parseInt));
    }};

}