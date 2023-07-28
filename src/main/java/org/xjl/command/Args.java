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
        return new OptionClass<T>(optionsClass).getT(args);
    }

    static class OptionClass<T> {
        private Class<T> optionsClass;

        public OptionClass(Class<T> optionsClass) {
            this.optionsClass = optionsClass;
        }

        private T getT(String[] args) {
            try {
                List<String> arguments = Arrays.asList(args);
                Constructor<?> constructor =  this.optionsClass.getDeclaredConstructors()[0];
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
    }

    private static Object parseOption(List<String> arguments, Parameter parameter) {
        return parseOption(arguments, parameter, parserMap);
    }

    private static Object parseOption(List<String> arguments, Parameter parameter, Map<Class<?>, OptionParser> parserMap1) {
        if (!parameter.isAnnotationPresent(Option.class)) {
            throw new IllegalOptionException(parameter.getName());
        }
        return parserMap1.get(parameter.getType()).parse(arguments, parameter.getAnnotation(Option.class));
    }

    private static Map<Class<?>, OptionParser> parserMap = new HashMap<Class<?>, OptionParser>(){{
        put(boolean.class, OptionParsers.bool());
        put(int.class, OptionParsers.unary((Integer) 0, Integer::parseInt));
        put(String.class, OptionParsers.unary("", String::valueOf));
        put(String[].class, OptionParsers.list(String[]::new, String::valueOf));
        put(Integer[].class, OptionParsers.list(Integer[]::new, Integer::parseInt));
    }};

}