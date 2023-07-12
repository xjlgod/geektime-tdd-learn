package org.xjl.command;

import java.util.List;
import java.util.function.Function;

class SingleValueOptionParser<T> implements Parser {
    private Function<String, T> parser;
    private T defaultValue;

    public SingleValueOptionParser(Function<String, T> parser, T defaultValue) {
        this.parser = parser;
        this.defaultValue = defaultValue;
    }

    @Override
    public T parse(List<String> arguments, Option option) {
        int index = arguments.indexOf("-" + option.value());
        if (arguments.size() == 0) {
            return defaultValue;
        }
        if (index + 1 == arguments.size() || arguments.get(index + 1).startsWith("-")) {
            throw new InsufficientArgumentsException(option.value());
        }
        if (index + 2 < arguments.size() && !arguments.get(index + 2).startsWith("-")) {
            throw new TooManyArgumentsException(option.value());
        }
        return parser.apply(arguments.get(index + 1));
    }
}
