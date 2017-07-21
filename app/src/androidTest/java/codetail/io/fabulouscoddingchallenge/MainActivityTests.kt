package codetail.io.fabulouscoddingchallenge

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewAssertion
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import codetail.io.fabulouscoddingchallenge.MainActivity.Companion.FLOATING_VIEW_DISSAPPEARS_UNTIL
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Before running those animation please do
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTests {

    @Rule @JvmField
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testFloatingViewAppearing() {
        onView(withText(R.string.floating_sample_text))
                .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun testFloatingViewDisappearsWhenReachesIndex() {
        onView(withId(R.id.contentView))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(FLOATING_VIEW_DISSAPPEARS_UNTIL + 1))

        onView(withText(R.string.floating_sample_text))
                .check(viewNotFound())

        onView(withId(R.id.secondaryAction))
                .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun testFloatingViewIsSwipeable() {
        onView(withText(R.string.floating_sample_text))
                .perform(swipeRight())

        onView(withText(R.string.floating_sample_text))
                .check(viewNotFound())

        onView(withId(R.id.secondaryAction))
                .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun testFloatingViewIsSwipeableOnlyRight() {
        onView(withText(R.string.floating_sample_text))
                .perform(swipeLeft())
                .perform(swipeUp())
                .perform(swipeDown())
                .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("codetail.io.fabulouscoddingchallenge", appContext.packageName)
    }


    companion object {
        fun viewNotFound(): ViewAssertion {
            return ViewAssertion { view, noViewFoundException ->
                Assert.assertNotNull("View actually still appearing", noViewFoundException)
            }
        }
    }
}
