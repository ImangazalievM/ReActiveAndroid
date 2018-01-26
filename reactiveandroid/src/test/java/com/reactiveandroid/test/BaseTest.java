package com.reactiveandroid.test;

import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(ReActiveAndroidTestRunner.class)
public abstract class BaseTest {

    @Rule
    public DataBaseTestRule dataBaseTestRule = DataBaseTestRule.create();

}
