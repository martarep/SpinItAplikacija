
package hr.algebra.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;


public class ReflectionUtils {

    public static void readClassInfo(Class<?> clazz, StringBuilder classInfo) {
        appendPackage(clazz, classInfo);
        appendModifiers(clazz, classInfo);
        appendParent(clazz, classInfo, true);
        appendInterfaces(clazz, classInfo);
    }

    private static void appendPackage(Class<?> clazz, StringBuilder classInfo) {
        classInfo
                .append("<br/>")
                .append("<br/>")
                .append("<h3> Package:  ")
                .append("</h3>")
                .append(clazz.getPackage())
                .append("<br/>")
                .append("<br/>");
    }

    private static void appendModifiers(Class<?> clazz, StringBuilder classInfo) {

        classInfo
                .append("<h3> Class:  ")
                .append("</h3>")
                .append(Modifier.toString(clazz.getModifiers()))
                .append(" ")
                .append(clazz.getSimpleName());
    }

    private static void appendParent(Class<?> clazz, StringBuilder classInfo, boolean first) {
        Class<?> parent = clazz.getSuperclass();
        if (parent == null) {
            return;
        }
        if (first) {
            classInfo.append("<br/>extends");
        }
        classInfo
                .append(" ")
                .append(parent.getName());
        appendParent(parent, classInfo, false);
    }

    private static void appendInterfaces(Class<?> clazz, StringBuilder classInfo) {
        if (clazz.getInterfaces().length > 0) {
            classInfo.append("<br/> implements");
        }
        for (Class<?> in : clazz.getInterfaces()) {
            classInfo
                    .append(" ")
                    .append(in.getName());
        }
    }

    public static void readClassAndMembersInfo(Class<?> clazz, StringBuilder classAndMembersInfo) {
        readClassInfo(clazz, classAndMembersInfo);
        appendFields(clazz, classAndMembersInfo);
        appendMethods(clazz, classAndMembersInfo);
        appendConstructors(clazz, classAndMembersInfo);
    }

    private static void appendFields(Class<?> clazz, StringBuilder classAndMembersInfo) {
        //Field[] fields = clazz.getFields(); // returns public and inherited
        Field[] fields = clazz.getDeclaredFields(); // returns public, protected, default (package) access, and private fields, but excludes inherited fields
        classAndMembersInfo.append("<br/>")
                .append("<br/>")
                .append("<h3> Declared fields:  ")
                .append("</h3>");

        for (Field field : fields) {
            classAndMembersInfo
                    .append(field)
                    .append("<br/>");
        }
    }

    private static void appendMethods(Class<?> clazz, StringBuilder classAndMembersInfo) {
        Method[] methods = clazz.getDeclaredMethods();
        classAndMembersInfo.append("<h3> Methods:  ")
                .append("</h3>");
        for (Method method : methods) {
            classAndMembersInfo.append("<br/>");
            appendMethodAnnotations(method, classAndMembersInfo);
            classAndMembersInfo
                    .append(Modifier.toString(method.getModifiers()))
                    .append(" ")
                    .append(method.getReturnType())
                    .append(" ")
                    .append(method.getName());
            appendParameters(method, classAndMembersInfo);
            appendExceptions(method, classAndMembersInfo);
        }
        classAndMembersInfo.append("<br/>");
    }

    private static void appendMethodAnnotations(Executable executable, StringBuilder classAndMembersInfo) {
        for (Annotation annotation : executable.getAnnotations()) {
            classAndMembersInfo
                    .append(annotation)
                    .append("<br/>");
        }
    }

    private static void appendParameters(Executable executable, StringBuilder classAndMembersInfo) {
        classAndMembersInfo.append("(");
        for (Parameter parameter : executable.getParameters()) {
            classAndMembersInfo
                    .append(parameter)
                    .append(", ");
        }
        if (classAndMembersInfo.toString().endsWith(", ")) {
            classAndMembersInfo.delete(classAndMembersInfo.length() - 2, classAndMembersInfo.length());
        }
        classAndMembersInfo.append(")");
    }

    private static void appendExceptions(Executable executable, StringBuilder classAndMembersInfo) {
        Class<?>[] exceptionTypes = executable.getExceptionTypes();
        if (exceptionTypes.length > 0) {
            classAndMembersInfo.append(" throws ");
            for (Class<?> exceptionType : exceptionTypes) {
                classAndMembersInfo
                        .append(exceptionType)
                        .append(", ");
            }
            if (classAndMembersInfo.toString().endsWith(", ")) {
                classAndMembersInfo.delete(classAndMembersInfo.length() - 2, classAndMembersInfo.length());
            }
        }
    }

    private static void appendConstructors(Class<?> clazz, StringBuilder classAndMembersInfo) {
        Constructor[] constructors = clazz.getDeclaredConstructors();
        classAndMembersInfo.append("<h3> Constructors:  ")
                .append("</h3>");
        for (Constructor constructor : constructors) {
            classAndMembersInfo.append("<br/>");
            appendMethodAnnotations(constructor, classAndMembersInfo);
            classAndMembersInfo
                    .append(Modifier.toString(constructor.getModifiers()))
                    .append(" ")
                    .append(constructor.getName());
            appendParameters(constructor, classAndMembersInfo);
            appendExceptions(constructor, classAndMembersInfo);
        }
    }

}
