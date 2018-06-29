package org.teinelund.application.commandline.addbuilder.commandline;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class Arguments {

    @Parameter(names = { "-p", "--path" }, description = "Path to java source file.", help = true, order = 1, required = true)
    private String filename;

    @Parameter(names = { "-v", "--version" }, description = "Show version.", help = true, order = 2)
    private boolean version = false;

    @Parameter(names = { "-h", "--help" }, description = "Show this help page.", help = true, order = 3)
    private boolean help = false;

    private JCommander jCommander;

    public String getFilename() {
        return filename;
    }

    public boolean isVersion() {
        return version;
    }

    public boolean isHelp() {
        return help;
    }

    void setJCommander(JCommander jCommander) {
        this.jCommander = jCommander;
    }

    public void printHelp() {
        this.jCommander.usage();
    }
}
