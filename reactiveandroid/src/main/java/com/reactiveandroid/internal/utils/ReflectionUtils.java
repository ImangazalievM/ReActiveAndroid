package com.reactiveandroid.internal.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import com.reactiveandroid.annotation.Column;
import com.reactiveandroid.annotation.QueryColumn;
import com.reactiveandroid.annotation.QueryModel;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.internal.log.LogLevel;
import com.reactiveandroid.internal.log.ReActiveLog;
import com.reactiveandroid.internal.serializer.TypeSerializer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

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
        return type.isAnnotationPresent(Table.class) && (!Modifier.isAbstract(type.getModifiers()));
    }

    public static boolean isTypeSerializer(Class<?> type) {
        return isSubclassOf(type, TypeSerializer.class);
    }

    public static List<Field> getDeclaredFields(Class<?> type) {
        List<Field> declaredFields = new ArrayList<>();
        Collections.addAll(declaredFields, type.getDeclaredFields());

        Class<?> parentType = type.getSuperclass();
        while (parentType != null) {
            Collections.addAll(declaredFields, parentType.getDeclaredFields());
            parentType = parentType.getSuperclass();
        }
        return declaredFields;
    }

    public static List<Class<?>> getDatabaseTableClasses(List<Class<?>> allClasses, Class<?> databaseClass) {
        List<Class<?>> modelClasses = new ArrayList<>();
        for (Class<?> modelClass : allClasses) {
            if (isModel(modelClass)) {
                ReActiveLog.e(LogLevel.BASIC, "Model loaded: " + modelClass.getSimpleName());
                Table tableAnnotation = modelClass.getAnnotation(Table.class);
                if (tableAnnotation.database() == databaseClass) {
                    modelClasses.add(modelClass);
                }
            }
        }
        return modelClasses;
    }

    @NonNull
    public static List<Class<?>> getDatabaseQueryModelClasses(List<Class<?>> allClasses, Class<?> databaseClass) {
        List<Class<?>> queryModelClasses = new ArrayList<>();
        for (Class<?> targetClass : allClasses) {
            QueryModel queryModelAnnotation = targetClass.getAnnotation(QueryModel.class);
            if (queryModelAnnotation == null) {
                continue;
            }
            if (queryModelAnnotation.database() == databaseClass) {
                queryModelClasses.add(targetClass);
            }
        }
        return queryModelClasses;
    }

    @NonNull
    public static List<Class<?>> getAllClasses(Context context) {
        try {
            List<String> allClassNames = getAllClassNames(context);
            return loadClasses(context, allClassNames);
        } catch (IOException | PackageManager.NameNotFoundException e) {
            ReActiveLog.e(LogLevel.BASIC, "Classes loading error", e);
        }
        return new ArrayList<>();
    }


    private static List<String> getAllClassNames(Context context) throws PackageManager.NameNotFoundException, IOException {
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

    private static List<Class<?>> loadClasses(Context context, List<String> classNames) {
        String packageName = context.getPackageName();
        List<Class<?>> discoveredClasses = new ArrayList<>();
        for (String className : classNames) {
            try {
                if (className.startsWith(packageName)) {
                    Class<?> discoveredClass = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
                    discoveredClasses.add(discoveredClass);
                }
            } catch (Throwable e) {
                ReActiveLog.e(LogLevel.BASIC, "Class when loading " + className);
                e.printStackTrace();
            }
        }
        return discoveredClasses;
    }

}