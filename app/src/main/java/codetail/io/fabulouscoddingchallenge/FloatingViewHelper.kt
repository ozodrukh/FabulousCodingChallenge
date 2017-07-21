package codetail.io.fabulouscoddingchallenge

import android.app.Activity
import android.support.annotation.LayoutRes
import android.support.annotation.UiThread
import android.view.LayoutInflater
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

    private fun findSuitableParent(where: View): FrameLayout {
        if (where is ViewGroup) {
            var p = where
            while (p !is FrameLayout) {
                if (p.parent == null) {
                    throw RuntimeException("Floating view couldn't find any suitable FrameLayout to be attached")
                }

                p = p.parent as ViewGroup
            }
            return p
        } else {
            return findSuitableParent(where.parent as ViewGroup)
        }
    }

    private fun findSuitableParent(where: Activity): FrameLayout {
        var parent = where.findViewById<ViewGroup>(android.R.id.content);
        if (parent !is FrameLayout) {
            parent = findSuitableParent(where.window.decorView)
        }
        return parent
    }

    /**
     * @return Floating view state controller with found suitable FrameLayout
     *
     * @throws RuntimeException if we couldn't find any suitable parent for floating
     * view
     */
    fun make(view: View, where: Activity): FloatingViewState {
        return FloatingViewState(view, findSuitableParent(where))
    }

    /**
     * @return Floating view state controller with found suitable FrameLayout
     *
     * @throws RuntimeException if we couldn't find any suitable parent for floating
     * view
     */
    fun make(view: View, where: View): FloatingViewState {
        return FloatingViewState(view, findSuitableParent(where))
    }

    /**
     * @return Floating view state controller with found suitable FrameLayout
     *
     * @throws RuntimeException if we couldn't find any suitable parent for floating
     * view
     */
    fun make(@LayoutRes layoutId: Int, where: View): FloatingViewState {
        val parent = findSuitableParent(where)
        val target = LayoutInflater.from(where.context).inflate(layoutId, parent, false)
        return FloatingViewState(target, parent)
    }

    /**
     * @return Floating view state controller with found suitable FrameLayout
     *
     * @throws RuntimeException if we couldn't find any suitable parent for floating
     * view
     */
    fun make(@LayoutRes layoutId: Int, where: Activity): FloatingViewState {
        val parent = findSuitableParent(where)
        val target = LayoutInflater.from(where).inflate(layoutId, parent, false)
        return FloatingViewState(target, parent)
    }

    class FloatingViewState(
            val target: View, /* Floating View */
            val parent: FrameLayout /* Ancestor of the floating view */
    ) {
        /* Callbacks for FloatingView state change */
        var onAttached: ((target: View, parent: View) -> Unit)? = null
        var onDetached: ((target: View, parent: View) -> Unit)? = null

        /**
         * @returns True if view is floating on screen (meaning it is attached to parent),
         * otherwise False
         */
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

        /**
         * Ensures that floating view is not displaying if satisfies circumstances
         * than it shows floating view on screen.
         *
         * Should be invoked only on UI Thread only
         *
         * @throws IllegalStateException
         */
        @UiThread
        fun show() {
            ensureState(false)

            // add view on top of all other children
            parent.addView(target)
            isShowing = true

            onAttached?.invoke(target, parent)
        }

        /**
         * Ensures view is displaying if so view floating view will be removed from
         * screen, otherwise throws IllegalStateException
         *
         * @throws IllegalStateException
         */
        @UiThread
        fun dismiss() {
            ensureState(true)

            // Remove it from view stack
            parent.removeView(target)
            isShowing = false

            onDetached?.invoke(target, parent)
        }
    }
}
