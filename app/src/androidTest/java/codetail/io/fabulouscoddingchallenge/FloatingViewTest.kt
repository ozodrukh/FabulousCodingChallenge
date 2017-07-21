package codetail.io.fabulouscoddingchallenge

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import codetail.io.fabulouscoddingchallenge.MainActivity.Companion.FLOATING_VIEW_DISSAPPEARS_UNTIL

/**
 * Before running those animation please do
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class FloatingViewTest {

    @Rule @JvmField
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun ActivityCreated_testFloatingViewAppearing() {
        onView(withText(R.string.floating_sample_text))
                .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun ActivityCreated_testFloatingViewDisappearsWhenReachesIndex() {
        onView(withId(R.id.contentView))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(FLOATING_VIEW_DISSAPPEARS_UNTIL + 1))

        onView(withText(R.string.floating_sample_text))
                .check { view, noViewFoundException ->
                    Assert.assertNotNull("View actually still appearing", noViewFoundException)
                }
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("codetail.io.fabulouscoddingchallenge", appContext.packageName)
    }
}
