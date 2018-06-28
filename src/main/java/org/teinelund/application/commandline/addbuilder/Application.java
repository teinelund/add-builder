package org.teinelund.application.commandline.addbuilder;

import com.beust.jcommander.JCommander;
import org.teinelund.application.commandline.addbuilder.commandline.Arguments;

public class Application
{
    public static void main( String[] args )
    {
        Arguments arguments = new Arguments();
        JCommander jCommander = JCommander.newBuilder().programName("add-builder").addObject(arguments).build();
        jCommander.parse(args);

        if (arguments.isHelp()) {
            jCommander.usage();
            return;
        }
        if (arguments.isVersion()) {
            System.out.println("add-builder version 1.0.0-SNAPSHOT. (c) Henrik Teinelund 2018.");
            System.out.println("");
            return;
        }
        System.out.println(arguments.getFilename());
    }
}
