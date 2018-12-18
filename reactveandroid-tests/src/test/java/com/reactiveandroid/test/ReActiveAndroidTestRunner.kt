package com.reactiveandroid.test

import android.os.Build

import com.reactiveandroid.BuildConfig

import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

import java.lang.reflect.Method

class ReActiveAndroidTestRunner @Throws(Exception::class) constructor(
        clazz: Class<*>
) : RobolectricTestRunner(clazz) {

    override fun getConfig(method: Method): Config {
        val defaultConfig = super.getConfig(method)
        return Config.Implementation(
                intArrayOf(SDK_EMULATE_LEVEL),
                defaultConfig.minSdk,
                defaultConfig.maxSdk,
                defaultConfig.manifest,
                defaultConfig.qualifiers,
                defaultConfig.packageName,
                defaultConfig.abiSplit,
                defaultConfig.resourceDir,
                defaultConfig.assetDir,
                defaultConfig.buildDir,
                defaultConfig.shadows::class.java.classes,
                defaultConfig.instrumentedPackages,
                TestApp::class.java, // Notice that we override real application class for Unit tests.
                defaultConfig.libraries,
                if (defaultConfig.constants == Void::class.java) BuildConfig::class.java else defaultConfig.constants::class.java
        )
    }

    companion object {

        private const val SDK_EMULATE_LEVEL = Build.VERSION_CODES.M

    }

}