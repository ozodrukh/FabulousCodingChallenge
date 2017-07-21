package codetail.io.fabulouscoddingchallenge

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import codetail.io.fabulouscoddingchallenge.ext.findView

/**
 * created at 7/21/17
 *
 * Some explanation on decisions, while this is Test application, i
 * guess there is no need in string translation, therefore all strings would
 * be hardcoded. In real world application all those things of course will
 * be in separate string.xml
 *
 * @author Ozodrukh
 * @version 1.0
 */

class MainActivity : AppCompatActivity() {
    companion object {
        const val FLOATING_VIEW_DISSAPPEARS_UNTIL = 30
    }

    lateinit var contentView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actviity_main)

        val floatingActionView = findView<FloatingActionButton>(R.id.secondaryAction)
        floatingActionView.setOnClickListener {
            Snackbar.make(floatingActionView, R.string.oops_text, Snackbar.LENGTH_SHORT)
                    .show()
        }

        contentView = findView(R.id.contentView)
        contentView.adapter = UserHabitGuideAdapter()
        contentView.addOnScrollListener(BubblesSwapOnScroll(this, floatingActionView).apply {
            switchWhenReachesScrollIndex = FLOATING_VIEW_DISSAPPEARS_UNTIL
        })
    }
}
