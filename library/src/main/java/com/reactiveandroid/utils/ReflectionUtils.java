package com.reactiveandroid.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.pm.PackageManager;

import com.reactiveandroid.Model;
import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.internal.log.LogLevel;
import com.reactiveandroid.internal.log.ReActiveLog;
import com.reactiveandroid.serializer.TypeSerializer;

public final class ReflectionUtils {

    public static boolean isSubclassOf(Class<?> type, Class<?> superClass) {
        if (type.getSuperclass() != null) {
            if (type.getSuperclass().equals(superClass)) {
                return true;
            }

            return isSubclassOf(type.getSuperclass(), superClass);
        }

        return false;
    }

    public static boolean isModel(Class<?> type) {
        return isSubclassOf(type, Model.class) && (!Modifier.isAbstract(type.getModifiers()));
    }

    public static boolean isTypeSerializer(Class<?> type) {
        return isSubclassOf(type, TypeSerializer.class);
    }

    public static Set<Field> getDeclaredColumnFields(Class<?> type) {
        Set<Field> declaredColumnFields = Collections.emptySet();

        if (ReflectionUtils.isSubclassOf(type, Model.class) || Model.class.equals(type)) {
            declaredColumnFields = new LinkedHashSet<>();

            Field[] fields = type.getDeclaredFields();
            Arrays.sort(fields, new Comparator<Field>() {
                @Override
                public int compare(Field field1, Field field2) {
                    return field2.getName().compareTo(field1.getName());
                }
            });

            for (Field field : fields) {
                if (field.isAnnotationPresent(Column.class)) {
                    declaredColumnFields.add(field);
                }
            }

            Class<?> parentType = type.getSuperclass();
            if (parentType != null) {
                declaredColumnFields.addAll(getDeclaredColumnFields(parentType));
            }
        }

        return declaredColumnFields;
    }

    public static List<Class> getDatabaseModelClasses(Context context, Class<?> databaseClass) {
        List<Class> modelClasses = new ArrayList<>();
        try {
            for (String className : getAllClasses(context)) {
                Class<?> modelClass = getModelClass(className);
                if (modelClass != null) {
                    Table tableAnnotation = getTableAnnotationOrThrow(modelClass);
                    if (tableAnnotation.database() == databaseClass) {
                        modelClasses.add(modelClass);
                    }
                }
            }
        } catch (IOException | PackageManager.NameNotFoundException e) {
            ReActiveLog.e(LogLevel.BASIC, "Models loading error", e);
        }
        return modelClasses;
    }

    private static List<String> getAllClasses(Context context) throws PackageManager.NameNotFoundException, IOException {
        String packageName = context.getPackageName();
        List<String> classNames = new ArrayList<>();
        try {
            List<String> allClasses = MultiDexHelper.getAllClasses(context);
            for (String classString : allClasses) {
                if (classString.startsWith(packageName)) classNames.add(classString);
            }
        } catch (NullPointerException e) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> urls = classLoader.getResources("");
            while (urls.hasMoreElements()) {
                List<String> fileNames = new ArrayList<>();
                String classDirectoryName = urls.nextElement().getFile();
                if (classDirectoryName.contains("bin") || classDirectoryName.contains("classes")
                        || classDirectoryName.contains("retrolambda")) {
                    File classDirectory = new File(classDirectoryName);
                    for (File filePath : classDirectory.listFiles()) {
                        populateFiles(filePath, fileNames, "");
                    }
                    for (String fileName : fileNames) {
                        if (fileName.startsWith(packageName)) classNames.add(fileName);
                    }
                }
            }
        }

        return classNames;
    }

    private static Class<?> getModelClass(String className) {
        Class<?> discoveredClass = null;
        try {
            discoveredClass = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
        } catch (Throwable e) {
            String error = (e.getMessage() == null) ? "getModelClass " + className + " error" : e.getMessage();
            ReActiveLog.e(LogLevel.BASIC, error);
        }

        if ((discoveredClass != null) && isModel(discoveredClass)) {
            ReActiveLog.i(LogLevel.BASIC, "Model class: " + discoveredClass.getSimpleName());
            return discoveredClass;
        } else {
            return null;
        }
    }

    private static void populateFiles(File path, List<String> fileNames, String parent) {
        if (path.isDirectory()) {
            for (File newPath : path.listFiles()) {
                if ("".equals(parent)) {
                    populateFiles(newPath, fileNames, path.getName());
                } else {
                    populateFiles(newPath, fileNames, parent + "." + path.getName());
                }
            }
        } else {
            String pathName = path.getName();
            String classSuffix = ".class";
            pathName = pathName.endsWith(classSuffix) ?
                    pathName.substring(0, pathName.length() - classSuffix.length()) : pathName;
            if ("".equals(parent)) {
                fileNames.add(pathName);
            } else {
                fileNames.add(parent + "." + pathName);
            }
        }
    }

    private static Table getTableAnnotationOrThrow(Class<?> modelClass) {
        if (!modelClass.isAnnotationPresent(Table.class)) {
            throw new IllegalArgumentException("Table annotation not found  in class " + modelClass.getName());
        }
        return modelClass.getAnnotation(Table.class);
    }

}