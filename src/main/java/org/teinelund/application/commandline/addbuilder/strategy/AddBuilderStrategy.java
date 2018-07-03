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
            if (classInfo.isValid()) {
                printBuilder(classInfo);
            }
            else {
                printError(classInfo);
            }
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
        boolean oneClassFound = false;
        boolean twoOrMoreClassesFound = false;
        boolean enumOrInterfaceFound = false;
        for (TypeDeclaration<?> typeDeclaration : types) {
            if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
                ClassOrInterfaceDeclaration classOrInterfaceDecl = (ClassOrInterfaceDeclaration) typeDeclaration;
                if (classOrInterfaceDecl.isInterface()) {
                    enumOrInterfaceFound = true;
                    continue;
                }
                if (oneClassFound) {
                    twoOrMoreClassesFound = true;
                    continue;
                }
                else {
                    oneClassFound = true;
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
                    classInfo = ClassInfo.validClassInfo(name, list);
                }
            }
            else {
                enumOrInterfaceFound = true;
                continue;
            }
        }
        if (twoOrMoreClassesFound) {
            classInfo = ClassInfo.twoOrMoreClassesFound();
        }
        else if (enumOrInterfaceFound && !oneClassFound) {
            classInfo = ClassInfo.interfaceOrEnumFound();
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

    void printError(ClassInfo classInfo) {
        if (classInfo.isIngterfaceOrEnumError()) {
            System.out.println("No class found. Type --help to diplay the help page.");
        }
        else if (classInfo.isTwoOrMoreStateError()) {
            System.out.println("Two or more classes found. Valid is one class per java source file. Type --help to diplay the help page.");
        }
    }


}

class ClassInfo {
    private String name;
    private List<FieldMember> members;
    private ClassInfoErrorState state;

    private ClassInfo(String name, List<FieldMember> members) {
        this.name = name;
        this.members = members;
        this.state = ClassInfoErrorState.VALID_STATE;
    }

    private ClassInfo(ClassInfoErrorState state) {
        this.state = state;
    }

    public static ClassInfo validClassInfo(String name, List<FieldMember> members) {
        return new ClassInfo(name, members);
    }

    public static ClassInfo twoOrMoreClassesFound() {
        return new ClassInfo(ClassInfoErrorState.TWO_OR_MORE_CLASSES_STATE);
    }

    public static ClassInfo interfaceOrEnumFound() {
        return new ClassInfo(ClassInfoErrorState.INTERFACE_OR_ENUM_STATE);
    }

    boolean isValid() {
        return this.state == ClassInfoErrorState.VALID_STATE;
    }

    boolean isTwoOrMoreStateError() {
        return this.state == ClassInfoErrorState.TWO_OR_MORE_CLASSES_STATE;
    }

    boolean isIngterfaceOrEnumError() {
        return this.state == ClassInfoErrorState.INTERFACE_OR_ENUM_STATE;
    }

    public String getName() {
        return name;
    }

    public List<FieldMember> getMembers() {
        return members;
    }
}

enum ClassInfoErrorState {VALID_STATE, TWO_OR_MORE_CLASSES_STATE, INTERFACE_OR_ENUM_STATE};

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