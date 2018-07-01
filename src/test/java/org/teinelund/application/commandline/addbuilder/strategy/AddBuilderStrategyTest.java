package org.teinelund.application.commandline.addbuilder.strategy;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class AddBuilderStrategyTest {

    private static FileSystem fs = null;
    private static AddBuilderStrategy sut = null;
    private static Path projectPath = null;
    private static Path someFileThatDoesNotExist = null;
    private static Path pomXmlPath = null;
    private static Path javaFilePath = null;

    @BeforeAll
    static void setup() throws IOException {
        fs = Jimfs.newFileSystem(Configuration.unix());
        sut = new AddBuilderStrategy(null);
        projectPath = fs.getPath("/Users/Cody/Projects/Project");
        someFileThatDoesNotExist = fs.getPath(projectPath.toString(), "someFile.txt");
        pomXmlPath = fs.getPath(projectPath.toString(), "pom.xml");
        javaFilePath = fs.getPath(projectPath.toString(), "Application.java");
        if (!Files.exists(projectPath)) {
            Files.createDirectories(projectPath);
            Files.createFile(pomXmlPath);
            Files.createFile(javaFilePath);
        }
    }

    /*
    @BeforeEach
    void initTest() throws IOException {
        if (!Files.exists(projectPath)) {
            Files.createDirectories(projectPath);
            Files.createFile(pomXmlPath);
            Files.createFile(javaFilePath);
        }
    }
    */

    /*
    @AfterEach
    void cleanUpTest() throws IOException {
        if (Files.exists(pomXmlPath)) {
            Files.delete(pomXmlPath);
        }
    }
    */

    /**
     * Help method to delete a directory recursevly. Apache IO's FileUtils.delete(...) does not work with Google
     * jimfs.
     *
     * @param path
     * @throws IOException
     */
    public static void deleteDirectory(Path path) throws IOException {
        DirectoryStream<Path> stream = Files.newDirectoryStream(path);
        for (Path fileOrDirectoryPath : stream) {
            if (Files.isRegularFile(fileOrDirectoryPath)) {
                Files.delete(fileOrDirectoryPath);
            }
            if (Files.isDirectory(fileOrDirectoryPath)) {
                deleteDirectory(fileOrDirectoryPath);
            }
        }
        Files.delete(path);
    }

    @Test
    public void verifyPathWherePathDoesNotExist() throws IOException {
        // Initialize
        // Test
        boolean result = sut.verifyPath(someFileThatDoesNotExist);
        // Verify
        assertThat(result).isFalse();
    }

    @Test
    public void verifyPathWherePathIsAFolder() throws IOException {
        // Initialize
        // Test
        boolean result = sut.verifyPath(projectPath);
        // Verify
        assertThat(result).isFalse();
    }

    @Test
    public void verifyPathWherePathIsNotAJavaFile() throws IOException {
        // Initialize
        // Test
        boolean result = sut.verifyPath(pomXmlPath);
        // Verify
        assertThat(result).isFalse();
    }

    @Test
    public void verifyPathWherePathIsAJavaFile() throws IOException {
        // Initialize
        // Test
        boolean result = sut.verifyPath(javaFilePath);
        // Verify
        assertThat(result).isFalse();
    }

}