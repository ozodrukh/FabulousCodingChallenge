package codetail.io.fabulouscoddingchallenge

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import codetail.io.fabulouscoddingchallenge.ext.*

import android.support.v7.appcompat.R.style.TextAppearance_AppCompat_Small_Inverse
import android.view.Gravity

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

        contentView = findView(R.id.contentView)
        contentView.adapter = UserHabitGuideAdapter()
        contentView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val floatingViewUntilIndex = 30
            var floatingViewState: FloatingViewHelper.FloatingViewState? = null

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

            }

            override fun onScrolled(parent: RecyclerView, dx: Int, dy: Int) {
                val layout = parent.layoutManager as LinearLayoutManager

                if (floatingViewUntilIndex >= layout.findLastCompletelyVisibleItemPosition()) {
                    if (floatingViewState == null) {
                        val floatingView = AppCompatTextView(parent.context).apply {
                            text = context.getString(R.string.floating_sample_text)
                            padding(16f)
                            customFrameLayoutParams(
                                    height = context.dp(64f),
                                    gravity = Gravity.BOTTOM,
                                    bottomMargin = context.dp(16f)
                            )
                            textAppearance(id = TextAppearance_AppCompat_Small_Inverse)
                            setBackgroundColor(colorOf(R.color.floatingViewBackgroundColor))
                        }

                        floatingViewState = FloatingViewHelper.make(floatingView, parent)
                    }

                    floatingViewState?.let {
                        if (!it.showing) {
                            it.show()
                        }
                    }
                } else {
                    floatingViewState?.let {
                        if (it.showing) {
                            it.dismiss()
                        }
                    }
                }
            }
        })
    }
}
