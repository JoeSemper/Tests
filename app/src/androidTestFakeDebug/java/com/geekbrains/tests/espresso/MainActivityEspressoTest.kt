package com.geekbrains.tests.espresso

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.geekbrains.tests.R
import com.geekbrains.tests.view.search.MainActivity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityEspressoTest {

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun activity_AssertNotNull() {
        scenario.onActivity {
            assertNotNull(it)
        }
    }

    @Test
    fun activity_IsResumed() {
        assertEquals(Lifecycle.State.RESUMED, scenario.state)
    }

    @Test
    fun activityEditText_NotNull() {
        scenario.onActivity {
            val editTextView = it.findViewById<EditText>(R.id.searchEditText)
            assertNotNull(editTextView)
        }
    }

    @Test
    fun activityEditText_IsDisplayed() {
        onView(withId(R.id.searchEditText)).check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun activityEditText_IsCompletelyDisplayed() {
        onView(withId(R.id.searchEditText)).check(matches(ViewMatchers.isCompletelyDisplayed()))
    }

    @Test
    fun activitySearch_IsWorking() {
        onView(withId(R.id.searchEditText)).perform(click())
        onView(withId(R.id.searchEditText)).perform(replaceText("algol"), closeSoftKeyboard())
        onView(withId(R.id.searchEditText)).perform(pressImeActionButton())
        onView(withId(R.id.totalCountTextView)).check(matches(withText("Number of results: 42")))
    }

    @Test
    fun activityTotalCountTextView_NotNull() {
        scenario.onActivity {
            val totalCountTextView = it.findViewById<TextView>(R.id.totalCountTextView)
            assertNotNull(totalCountTextView)
        }
    }

    @Test
    fun activityTotalCountTextView_IsDisplayed() {
        onView(withId(R.id.searchEditText)).perform(click())
        onView(withId(R.id.searchEditText)).perform(replaceText("algol"), closeSoftKeyboard())
        onView(withId(R.id.searchEditText)).perform(pressImeActionButton())
        onView(withId(R.id.toDetailsActivityButton)).check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun activityTotalCountTextView_IsCompletelyDisplayed() {
        onView(withId(R.id.searchEditText)).perform(click())
        onView(withId(R.id.searchEditText)).perform(replaceText("algol"), closeSoftKeyboard())
        onView(withId(R.id.searchEditText)).perform(pressImeActionButton())
        onView(withId(R.id.toDetailsActivityButton)).check(matches(ViewMatchers.isCompletelyDisplayed()))
    }

    @Test
    fun activityToDetailsButton_NotNull() {
        scenario.onActivity {
            val button = it.findViewById<Button>(R.id.toDetailsActivityButton)
            assertNotNull(button)
        }
    }

    @Test
    fun activityToDetailsButton_IsDisplayed() {
        onView(withId(R.id.toDetailsActivityButton)).check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun activityToDetailsButton_IsCompletelyDisplayed() {
        onView(withId(R.id.toDetailsActivityButton)).check(matches(ViewMatchers.isCompletelyDisplayed()))
    }

    @Test
    fun activityToDetailsButton_CorrectText() {
        scenario.onActivity {
            val button = it.findViewById<Button>(R.id.toDetailsActivityButton)
            val buttonText = it.getString(R.string.to_details)
            assertEquals(buttonText, button.text)
        }
    }

    @After
    fun close() {
        scenario.close()
    }
}