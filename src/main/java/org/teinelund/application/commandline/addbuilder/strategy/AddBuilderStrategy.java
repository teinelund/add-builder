package org.teinelund.application.commandline.addbuilder.strategy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AddBuilderStrategy implements Strategy {

    private String fileName;

    public AddBuilderStrategy(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void doAction() {
        Path path = Paths.get(this.fileName);
        verifyPath(path);
    }

    boolean verifyPath(Path path) {
        if (!Files.exists(path)) {
            System.out.println("File \"" + path.toString() + "\" does not exist. Check spelling. Type --help to diplay the help page.");
            return false;
        }
        if (!Files.isRegularFile(path)) {
            System.out.println("File \"" + path.toString() + "\" is not a regular file. Check spelling. Type --help to diplay the help page.");
            return false;
        }
        if (!path.endsWith(".java")) {
            System.out.println("File \"" + path.toString() + "\" is not a java file. Check spelling. Type --help to diplay the help page.");
            return false;
        }
        return true;
    }
}
