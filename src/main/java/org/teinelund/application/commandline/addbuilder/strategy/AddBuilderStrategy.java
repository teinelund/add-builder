package org.teinelund.application.commandline.addbuilder.strategy;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class AddBuilderStrategy implements Strategy {

    private String fileName;

    public AddBuilderStrategy(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void doAction() throws IOException {
        Path path = Paths.get(this.fileName);
        if (verifyPath(path)) {
            BufferedReader reader = Files.newBufferedReader(path);
            ClassInfo classInfo = fetchFieldMembersFromJavaSourceFile(reader);
            printBuilder(classInfo);
        }
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
        String fileName = path.getFileName().toString();
        if (!fileName.endsWith(".java")) {
            System.out.println("File \"" + path.toString() + "\" is not a java file. Check spelling. Type --help to diplay the help page.");
            return false;
        }
        return true;
    }

    ClassInfo fetchFieldMembersFromJavaSourceFile(Reader reader) {
        List<FieldMember> list = new LinkedList<>();
        CompilationUnit compilationUnit = JavaParser.parse(reader);
        NodeList<TypeDeclaration<?>> types = compilationUnit.getTypes();
        ClassInfo classInfo = null;
        for (TypeDeclaration<?> typeDeclaration : types) {
            if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
                ClassOrInterfaceDeclaration classOrInterfaceDecl = (ClassOrInterfaceDeclaration) typeDeclaration;
                if (classOrInterfaceDecl.isInterface()) {
                    continue;
                }
                String name = classOrInterfaceDecl.getNameAsString();
                List<FieldDeclaration> fields = classOrInterfaceDecl.getFields();
                for (FieldDeclaration field : fields) {
                    NodeList<VariableDeclarator> variableDeclarators = field.getVariables();
                    for (VariableDeclarator variableDeclarator : variableDeclarators) {
                        list.add(new FieldMember(variableDeclarator.getType().asString(), variableDeclarator.getName().asString()));
                    }
                }
                if (!list.isEmpty()) {
                    classInfo = new ClassInfo(name, list);
                    break;
                }
            }
        }
        return classInfo;
    }

    void printBuilder(ClassInfo classInfo) {
        StringBuilder sb = new StringBuilder();
        // Constructor
        // App(AppBuilder builder) { .. }
        sb.append(classInfo.getName() + "(" + classInfo.getName() + "Builder builder) {");  sb.append(java.lang.System.lineSeparator());
        for (FieldMember fieldMember : classInfo.getMembers()) {
            sb.append("   this." + fieldMember.getName() + " = builder." + fieldMember.getName() + ";");   sb.append(java.lang.System.lineSeparator());
        }
        sb.append("}");  sb.append(java.lang.System.lineSeparator());
        sb.append(java.lang.System.lineSeparator());
        sb.append(java.lang.System.lineSeparator());
        // public static App.AppBuilder builder() { .. }
        sb.append("public static " + classInfo.getName() + "." + classInfo.getName() + "Builder builder() {");  sb.append(java.lang.System.lineSeparator());
        sb.append("   return new " + classInfo.getName() + "." + classInfo.getName() + "Builder();");  sb.append(java.lang.System.lineSeparator());
        sb.append("}");  sb.append(java.lang.System.lineSeparator());
        sb.append(java.lang.System.lineSeparator());
        sb.append(java.lang.System.lineSeparator());
        // The Builder:
        // public static class App.Builder { .. }
        sb.append("public static class " + classInfo.getName() + "Builder {");   sb.append(java.lang.System.lineSeparator());
        for (FieldMember fieldMember : classInfo.getMembers()) {
            sb.append("   private " + fieldMember.getType() + " " + fieldMember.getName() + ";");   sb.append(java.lang.System.lineSeparator());
        }
        sb.append(java.lang.System.lineSeparator());
        for (FieldMember fieldMember : classInfo.getMembers()) {
            // setter
            sb.append("   public " + classInfo.getName() + "." + classInfo.getName() + "Builder set" +
                    StringUtils.capitalize(fieldMember.getName()) + "(" + fieldMember.getType() + " " +
                    classInfo.getName() + ") {");   sb.append(java.lang.System.lineSeparator());
            sb.append("      this." + fieldMember.getName() + " = " + fieldMember.getName() + ";");  sb.append(java.lang.System.lineSeparator());
            sb.append("      return this;");  sb.append(java.lang.System.lineSeparator());
            sb.append("   }");  sb.append(java.lang.System.lineSeparator());
            sb.append(java.lang.System.lineSeparator());
        }


        sb.append("}");  sb.append(java.lang.System.lineSeparator());

        System.out.println(sb.toString());
    }


}

class ClassInfo {
    private String name;
    private List<FieldMember> members;

    public ClassInfo(String name, List<FieldMember> members) {
        this.name = name;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public List<FieldMember> getMembers() {
        return members;
    }
}

class FieldMember {
    private String type;
    private String name;

    public FieldMember(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}