package org.teinelund.application.commandline.addbuilder.commandline;

import com.beust.jcommander.JCommander;

public class ArgumentParser {

    private Arguments arguments;

    private ArgumentParser(String[] args) {
        arguments = new Arguments();
        JCommander jCommander = JCommander.newBuilder().programName("add-builder").addObject(arguments).build();
        jCommander.parse(args);
        arguments.setJCommander(jCommander);
    }

    public static ArgumentParser parseCommandLineArguments(String[] args) {
        return new ArgumentParser(args);
    }

    public Arguments getArguments() {
        return this.arguments;
    }
}
