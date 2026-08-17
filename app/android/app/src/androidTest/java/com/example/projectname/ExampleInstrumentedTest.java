package com.example.projectname;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Example instrumented test that executes on an Android device. */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void usesAppContext() {
        final Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.example.projectname", appContext.getPackageName());
    }
}
