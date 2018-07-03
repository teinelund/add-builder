package org.teinelund.application.commandline.addbuilder;

import org.teinelund.application.commandline.addbuilder.commandline.ArgumentParser;
import org.teinelund.application.commandline.addbuilder.commandline.Arguments;
import org.teinelund.application.commandline.addbuilder.controller.Controller;

import java.io.IOException;

public class Application
{
    public static void main( String[] args ) throws IOException {
        ArgumentParser parser = ArgumentParser.parseCommandLineArguments(args);
        Arguments arguments = parser.getArguments();
        Controller controller = new Controller(arguments);
        controller.selectCommandStrategy();
    }
}
