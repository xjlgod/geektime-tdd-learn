package org.xjl.command;

public class OptionsWithOutAnnotation {
    private boolean logging;
    private int port;

    public OptionsWithOutAnnotation(@Option("l") boolean logging, int port, @Option("d") String directory) {
        this.logging = logging;
        this.port = port;
        this.directory = directory;
    }

    private String directory;


    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean getLogging() {
        return logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }
}
