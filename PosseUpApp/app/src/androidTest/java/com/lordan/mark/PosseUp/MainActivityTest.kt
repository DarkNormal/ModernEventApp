package com.lordan.mark.PosseUp

import android.os.SystemClock

import android.support.test.espresso.ViewAssertion
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View

import com.lordan.mark.PosseUp.UI.MainActivityGroup.MainActivity

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import java.util.concurrent.TimeUnit

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withText

/**
 * Created by Mark on 26/04/2016.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @Rule @JvmField val main = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Test
    fun launchMainActivity() {
        onView(withText("Classic car show")).check(ViewAssertions.matches(isDisplayed()))
    }
}
