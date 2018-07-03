package org.teinelund.application.commandline.addbuilder.strategy;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
        assertThat(result).isTrue();
    }


    @Test
    public void fetchFieldMembersFromJavaSourceFile() {
        // Initialize
        Reader reader = createJavaSourceFile(JavaSourceFileState.ONE_CLASS);
        // Test
        ClassInfo result = sut.fetchFieldMembersFromJavaSourceFile(reader);
        // Verify
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isTrue();
        assertThat(result.getMembers().size()).isEqualTo(3);
        assertThat(result.getMembers().get(0).getType()).isEqualTo("String");
        assertThat(result.getMembers().get(0).getName()).isEqualTo("name");
        assertThat(result.getMembers().get(2).getType()).isEqualTo("List<Collegue>");
        assertThat(result.getMembers().get(2).getName()).isEqualTo("collegues");
    }

    enum JavaSourceFileState {ONE_CLASS, ONE_NON_PUBLIC_CLASS, INTERFACE, ENUM, TWO_CLASSES_PUBLIC_FIRST};

    Reader createJavaSourceFile(JavaSourceFileState state) {
        StringBuilder sb = new StringBuilder();
        sb.append("package org.teinelund;");
        sb.append(java.lang.System.lineSeparator());
        sb.append("");
        sb.append(java.lang.System.lineSeparator());
        sb.append("import java.io.IOException;");
        sb.append(java.lang.System.lineSeparator());
        sb.append("");
        sb.append(java.lang.System.lineSeparator());
        if(state == JavaSourceFileState.ONE_CLASS ||
                state == JavaSourceFileState.TWO_CLASSES_PUBLIC_FIRST ||
                state == JavaSourceFileState.ONE_NON_PUBLIC_CLASS) {
            if (state == JavaSourceFileState.ONE_CLASS || state == JavaSourceFileState.TWO_CLASSES_PUBLIC_FIRST) {
                sb.append("public class Company {");
            }
            else if (state == JavaSourceFileState.ONE_NON_PUBLIC_CLASS) {
                sb.append("class Company {");
            }
            sb.append(java.lang.System.lineSeparator());
            sb.append("");
            sb.append(java.lang.System.lineSeparator());
            sb.append("   private String name;");
            sb.append(java.lang.System.lineSeparator());
            sb.append("   private Address address;");
            sb.append(java.lang.System.lineSeparator());
            sb.append("   private List<Collegue> collegues;");
            sb.append(java.lang.System.lineSeparator());
            sb.append("");
            sb.append(java.lang.System.lineSeparator());
            sb.append("   public void setName(String name) {");
            sb.append(java.lang.System.lineSeparator());
            sb.append("      int x = 1;");
            sb.append(java.lang.System.lineSeparator());
            sb.append("   }");
            sb.append(java.lang.System.lineSeparator());
            sb.append("}");
            sb.append(java.lang.System.lineSeparator());
            if (state == JavaSourceFileState.TWO_CLASSES_PUBLIC_FIRST) {
                sb.append("");
                sb.append(java.lang.System.lineSeparator());
                sb.append("class User {");
                sb.append(java.lang.System.lineSeparator());
                sb.append("");
                sb.append(java.lang.System.lineSeparator());
                sb.append("   private String name;");
                sb.append(java.lang.System.lineSeparator());
                sb.append("");
                sb.append(java.lang.System.lineSeparator());
                sb.append("   public User(String name) {");
                sb.append(java.lang.System.lineSeparator());
                sb.append("      this.name = name;");
                sb.append(java.lang.System.lineSeparator());
                sb.append("   }");
                sb.append(java.lang.System.lineSeparator());
                sb.append("}");
                sb.append(java.lang.System.lineSeparator());
            }
        }
        else if (state == JavaSourceFileState.INTERFACE) {
            sb.append("public interface ICompany {"); sb.append(java.lang.System.lineSeparator());
            sb.append(""); sb.append(java.lang.System.lineSeparator());
            sb.append("   public void setName(String name);"); sb.append(java.lang.System.lineSeparator());
            sb.append("}"); sb.append(java.lang.System.lineSeparator());
        }
        else if (state == JavaSourceFileState.ENUM) {
            sb.append("public enum State {USER, VIP_USER};"); sb.append(java.lang.System.lineSeparator());
        }
        return new StringReader(sb.toString());
    }

    @Test
    public void fetchFieldMembersFromJavaSourceFileContainingInterface() {
        // Initialize
        Reader reader = createJavaSourceFile(JavaSourceFileState.INTERFACE);
        // Test
        ClassInfo result = sut.fetchFieldMembersFromJavaSourceFile(reader);
        // Verify
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isFalse();
        assertThat(result.isIngterfaceOrEnumError()).isTrue();
    }

    @Test
    public void fetchFieldMembersFromJavaSourceFileContainingEnum() {
        // Initialize
        Reader reader = createJavaSourceFile(JavaSourceFileState.ENUM);
        // Test
        ClassInfo result = sut.fetchFieldMembersFromJavaSourceFile(reader);
        // Verify
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isFalse();
        assertThat(result.isIngterfaceOrEnumError()).isTrue();
    }

    @Test
    public void fetchFieldMembersFromJavaSourceFileContainingNonPublicClass() {
        // Initialize
        Reader reader = createJavaSourceFile(JavaSourceFileState.ONE_NON_PUBLIC_CLASS);
        // Test
        ClassInfo result = sut.fetchFieldMembersFromJavaSourceFile(reader);
        // Verify
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isTrue();
        assertThat(result.getMembers().size()).isEqualTo(3);
        assertThat(result.getMembers().get(0).getType()).isEqualTo("String");
        assertThat(result.getMembers().get(0).getName()).isEqualTo("name");
        assertThat(result.getMembers().get(2).getType()).isEqualTo("List<Collegue>");
        assertThat(result.getMembers().get(2).getName()).isEqualTo("collegues");
    }

    @Test
    public void fetchFieldMembersFromJavaSourceFileContainingTwoClasses() {
        // Initialize
        Reader reader = createJavaSourceFile(JavaSourceFileState.TWO_CLASSES_PUBLIC_FIRST);
        // Test
        ClassInfo result = sut.fetchFieldMembersFromJavaSourceFile(reader);
        // Verify
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isFalse();
        assertThat(result.isTwoOrMoreStateError()).isTrue();
    }

}