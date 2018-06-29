package org.teinelund.application.commandline.addbuilder.strategy;

public class AddBuilderStrategy implements Strategy {

    private String fileName;

    public AddBuilderStrategy(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void doAction() {
        System.out.println(this.fileName);
    }
}
