package org.teinelund.application.commandline.addbuilder.controller;

import org.teinelund.application.commandline.addbuilder.commandline.Arguments;
import org.teinelund.application.commandline.addbuilder.strategy.AddBuilderStrategy;
import org.teinelund.application.commandline.addbuilder.strategy.HelpStrategy;
import org.teinelund.application.commandline.addbuilder.strategy.Strategy;
import org.teinelund.application.commandline.addbuilder.strategy.VersionStrategy;

import java.io.IOException;

public class Controller {
    private Arguments arguments;

    public Controller(Arguments arguments) {
        this.arguments = arguments;
    }

    public void selectCommandStrategy() throws IOException {
        Strategy strategy = null;
        if (arguments.isHelp()) {
            strategy = new HelpStrategy(this.arguments);
        }
        else if (arguments.isVersion()) {
            strategy = new VersionStrategy();
        }
        else {
            strategy = new AddBuilderStrategy(this.arguments.getFilename());
        }
        strategy.doAction();
    }
}
