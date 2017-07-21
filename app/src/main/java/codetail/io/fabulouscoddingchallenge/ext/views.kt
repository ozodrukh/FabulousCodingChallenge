package codetail.io.fabulouscoddingchallenge.ext

import android.app.Activity
import android.support.annotation.IdRes
import android.support.v4.widget.TextViewCompat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.ViewTreeObserver
import android.widget.FrameLayout

/**
 * @author Ozodrukh
 * @version 1.0
 */

fun <T : View> Activity.findView(@IdRes id: Int): T {
    return findViewById<T>(id);
}

fun TextView.textAppearance(id: Int) {
    TextViewCompat.setTextAppearance(this, id)
}

fun View.onceMeasured(block: View.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    block()
                }
            })
}

fun TextView.padding(padding: Float) {
    val dpPadding = context.dp(padding)
    setPadding(dpPadding, dpPadding, dpPadding, dpPadding)
}

fun View.customLayoutParams(width: Int = MATCH_PARENT,
                            height: Int = WRAP_CONTENT): ViewGroup.LayoutParams {
    return ViewGroup.LayoutParams(width, height).apply {
        layoutParams = this /* Apply new layout params to View */
    }
}

fun View.customFrameLayoutParams(width: Int = MATCH_PARENT,
                                 height: Int = WRAP_CONTENT,
                                 leftMargin: Int = 0,
                                 topMargin: Int = 0,
                                 rightMargin: Int = 0,
                                 bottomMargin: Int = 0,
                                 gravity: Int = Gravity.NO_GRAVITY): FrameLayout.LayoutParams {

    return FrameLayout.LayoutParams(width, height).apply {
        layoutParams = this /* Apply new layout params to View */

        this.gravity = gravity
        this.leftMargin = leftMargin
        this.topMargin = topMargin
        this.rightMargin = rightMargin
        this.bottomMargin = bottomMargin
    }
}

fun View.customMarginLayoutParams(width: Int = MATCH_PARENT,
                                  height: Int = WRAP_CONTENT,
                                  leftMargin: Int = 0,
                                  topMargin: Int = 0,
                                  rightMargin: Int = 0,
                                  bottomMargin: Int = 0): ViewGroup.MarginLayoutParams {

    return ViewGroup.MarginLayoutParams(width, height).apply {
        layoutParams = this /* Apply new layout params to View */

        this.leftMargin = leftMargin
        this.topMargin = topMargin
        this.rightMargin = rightMargin
        this.bottomMargin = bottomMargin
    }
}