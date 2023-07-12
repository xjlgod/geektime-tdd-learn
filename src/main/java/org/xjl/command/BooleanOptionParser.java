package org.xjl.command;

import java.util.List;

class BooleanOptionParser implements Parser {
    @Override
    public Object parse(List<String> arguments, Option option) {
        int index =  arguments.indexOf("-" + option.value());
        if (index + 1 < arguments.size() &&
                !arguments.get(index + 1).startsWith("-")) {
            throw new TooManyArgumentsException(option.value());
        }
        return arguments.contains("-" + option.value());
    }
}
