package org.xjl.command;

import java.util.List;

interface OptionParser<T> {
    public T parse(List<String> arguments, Option option);
}
