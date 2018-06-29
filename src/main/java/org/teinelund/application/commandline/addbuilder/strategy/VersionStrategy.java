package org.teinelund.application.commandline.addbuilder.strategy;

public class VersionStrategy implements Strategy {
    @Override
    public void doAction() {
        System.out.println("add-builder version 1.0.0-SNAPSHOT. (c) Henrik Teinelund 2018.");
        System.out.println("");
    }
}
