package com.reactiveandroid.test;

import android.os.Build;
import android.support.annotation.NonNull;

import com.reactiveandroid.BuildConfig;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Method;

public class ReActiveAndroidTestRunner extends RobolectricTestRunner {

    private static final int SDK_EMULATE_LEVEL = Build.VERSION_CODES.M;

    public ReActiveAndroidTestRunner(@NonNull Class<?> clazz) throws Exception {
        super(clazz);
    }

    @Override
    public Config getConfig(@NonNull Method method) {
        final Config defaultConfig = super.getConfig(method);
        return new Config.Implementation(
                new int[]{SDK_EMULATE_LEVEL},
                defaultConfig.manifest(),
                defaultConfig.qualifiers(),
                defaultConfig.packageName(),
                defaultConfig.abiSplit(),
                defaultConfig.resourceDir(),
                defaultConfig.assetDir(),
                defaultConfig.buildDir(),
                defaultConfig.shadows(),
                defaultConfig.instrumentedPackages(),
                TestApp.class, // Notice that we override real application class for Unit tests.
                defaultConfig.libraries(),
                defaultConfig.constants() == Void.class ? BuildConfig.class : defaultConfig.constants()
        );
    }

}