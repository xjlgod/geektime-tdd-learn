package org.xjl.command;
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object parseOption(List<String> arguments, Parameter parameter) {
        return parserMap.get(parameter.getType()).parseOption(arguments, parameter.getAnnotation(Option.class));
    }

    private static Map<Class<?>, Parser> parserMap = new HashMap<Class<?>, Parser>(){{
        put(boolean.class, new BooleanOptionParser());
        put(int.class, new SingleValueOptionParser<>(Integer::parseInt));
        put(String.class, new SingleValueOptionParser<>(String::valueOf));
    }};

}