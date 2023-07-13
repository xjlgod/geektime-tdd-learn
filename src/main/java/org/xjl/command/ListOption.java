package org.xjl.command;

public class ListOption {
    private String[] group;
    private Integer[] decimals;

    public ListOption(@Option("g") String[] group, @Option("d") Integer[] decimals) {
        this.group = group;
        this.decimals = decimals;
    }

    public String[] getGroup() {
        return group;
    }

    public Integer[] getDecimals() {
        return decimals;
    }
}
