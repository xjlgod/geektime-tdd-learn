package org.xjl.command;

import java.util.List;
import java.util.function.Function;

class SingleValueOptionParser<T> implements Parser {
    private Function<String, T> parser;

    public SingleValueOptionParser(Function<String, T> parser) {
        this.parser = parser;
    }

    @Override
    public T parseOption(List<String> arguments, Option option) {
        int index = arguments.indexOf("-" + option.value());
        return parser.apply(arguments.get(index + 1));
    }
}
