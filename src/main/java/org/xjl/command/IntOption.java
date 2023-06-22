package org.xjl.command;

public class IntOption {
    private int port;

    public IntOption(@Option("p") int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }
}
