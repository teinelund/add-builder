package org.teinelund.application.commandline.addbuilder.strategy;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileReader;
import java.io.IOException;

public class VersionStrategy implements Strategy {
    @Override
    public void doAction() {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = null;
        try {
            model = reader.read(new FileReader("pom.xml"));
            String version = model.getVersion();
            System.out.println("add-builder version " + version + ". (c) Henrik Teinelund 2018.");
            System.out.println("");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }
}
