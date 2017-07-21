package codetail.io.fabulouscoddingchallenge

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * FloatingViewHelper shares same idea as normal SnackBar it attaches
 * floating view in Activity DecorView or any parent you desire but with
 * one rule parent must FrameLayout
 *
 * @author Ozodrukh
 * @version 1.0
 */
object FloatingViewHelper {

    fun make(view: View, where: Activity): FloatingViewState {
        return make(view, where.window.decorView)
    }

    fun make(view: View, where: View): FloatingViewState {
        if (where is ViewGroup) {
            var p = where
            while (p !is FrameLayout) {
                if (p.parent == null) {
                    throw RuntimeException("Floating view couldn't find any suitable FrameLayout to be attached")
                }

                p = p.parent as ViewGroup
            }

            return FloatingViewState(view, p)
        } else {
            return make(view, where.parent as ViewGroup)
        }
    }

    class FloatingViewState(
            val target: View, /* Floating View */
            val parent: FrameLayout /* Ancestor of the floating view */
    ) {
        var onAttached: ((target: View, parent: View) -> Unit)? = null
        var onDetached: ((target: View, parent: View) -> Unit)? = null

        private val attachView = Runnable {
            // add view on top of all other children
            parent.addView(target)
            isShowing = true

            onAttached?.invoke(target, parent)
        }

        private val detachView = Runnable {
            // Remove it from view stack
            parent.removeView(target)
            isShowing = false

            onDetached?.invoke(target, parent)
        }

        val showing: Boolean
            get() = isShowing

        private var isShowing = false

        private fun ensureState(showing: Boolean) {
            if (isShowing != showing) {
                if (isShowing) {
                    throw IllegalStateException("View is already floating, " +
                            "check state using FloatingViewState#showing property")
                } else {
                    throw IllegalStateException("View expected to be floating, " +
                            "but unfortunately he is not :(")
                }
            }
        }

        fun show() {
            ensureState(false)

            /**
             * post to UI frame thread & remove previously posted attaching from queue
             */
            parent.removeCallbacks(attachView)
            parent.post(attachView)
        }

        fun dismiss() {
            ensureState(true)

            parent.removeCallbacks(detachView)
            parent.post(detachView)
        }
    }
}
