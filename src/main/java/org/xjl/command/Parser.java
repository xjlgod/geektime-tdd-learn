package org.xjl.command;

import java.util.List;

interface Parser {
    public Object parse(List<String> arguments, Option option);
}
