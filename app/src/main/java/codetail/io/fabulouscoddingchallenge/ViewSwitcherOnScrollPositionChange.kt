package codetail.io.fabulouscoddingchallenge

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * created at 7/21/17
 *
 * @author Ozodrukh
 * @version 1.0
 */
abstract class ViewSwitcherOnScrollPositionChange : RecyclerView.OnScrollListener() {

    var switchWhenReachesScrollIndex: Int = -1

    private var expectPrimaryViewVisible = true
    private var secondaryViewCreated = false

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

    }

    override fun onScrolled(parent: RecyclerView, dx: Int, dy: Int) {
        val layout = parent.layoutManager as LinearLayoutManager

        if (switchWhenReachesScrollIndex >= layout.findLastCompletelyVisibleItemPosition()) {
            flipViews(false)
        } else {
            flipViews(true)
        }
    }

    private fun flipViews(showSecondaryView: Boolean) {
        if (!secondaryViewCreated) {
            createSecondaryView()
            secondaryViewCreated = true
        }

        onViewFlipping(showSecondaryView)

        expectPrimaryViewVisible = !showSecondaryView
    }

    /**
     * Create second view, it going to appear now
     */
    protected abstract fun createSecondaryView()

    /**
     * Change appearing views
     */
    protected abstract fun onViewFlipping(showSecondaryView: Boolean);
}
