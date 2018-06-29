package org.teinelund.application.commandline.addbuilder.strategy;

import org.teinelund.application.commandline.addbuilder.commandline.Arguments;

public class HelpStrategy implements Strategy {
    private Arguments arguments;

    public HelpStrategy(Arguments arguments) {
        this.arguments = arguments;
    }

    @Override
    public void doAction() {
        arguments.printHelp();
    }
}
