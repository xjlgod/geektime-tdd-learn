package org.xjl.command;

import java.lang.reflect.Parameter;
import java.util.List;

interface Parser {
    public Object parseOption(List<String> arguments, Option option);
}
