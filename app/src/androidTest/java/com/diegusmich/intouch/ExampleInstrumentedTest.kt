package com.diegusmich.intouch

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.diegusmich.intouch.ui.activities.AddPostActivity
import com.diegusmich.intouch.ui.activities.AuthActivity
import com.diegusmich.intouch.ui.activities.EventActivity
import com.diegusmich.intouch.ui.activities.MainActivity

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
@RunWith(AndroidJUnit4ClassRunner::class)
class ExampleInstrumentedTest{

    private lateinit var activityScenario : ActivityScenario<MainActivity>

    @Test
    fun setUp(){
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
    }
    @Test
    fun test() {
        onView(withId(R.id.submitFormPost)).perform(click())
    }
}