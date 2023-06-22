package org.xjl.command;

public class StringOption {
    private String directory;

    public StringOption(@Option("d") String directory) {
        this.directory = directory;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }
}
