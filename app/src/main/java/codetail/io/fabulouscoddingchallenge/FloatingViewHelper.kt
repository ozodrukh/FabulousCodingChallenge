package codetail.io.fabulouscoddingchallenge

import android.app.Activity
import android.graphics.Rect
import android.support.annotation.LayoutRes
import android.support.annotation.UiThread
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ViewDragHelper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import codetail.io.fabulouscoddingchallenge.ext.customFrameLayoutParams
import timber.log.Timber

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
        var onDismissed: (() -> Unit)? = null

        private val shadowTarget = SwipeableLayout(target)

        init {
            shadowTarget.dismissCallback = {
                dismiss()

                onDismissed?.invoke()
            }
        }

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
            parent.addView(shadowTarget)
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
            parent.removeView(shadowTarget)
            isShowing = false

            onDetached?.invoke(target, parent)
        }
    }

    private class SwipeableLayout(val target: View) : FrameLayout(target.context) {
        companion object {
            private val DEFAULT_DRAG_DISMISS_THRESHOLD = 0.5f
            private val DEFAULT_ALPHA_START_DISTANCE = 0f
            private val DEFAULT_ALPHA_END_DISTANCE = DEFAULT_DRAG_DISMISS_THRESHOLD

            internal fun Float.clamp(min: Float, max: Float): Float {
                return Math.min(Math.max(min, this), max)
            }

            internal fun Int.clamp(min: Int, max: Int): Int {
                return Math.min(Math.max(min, this), max)
            }

            /**
             * The fraction that `value` is between `startValue` and `endValue`.
             */
            internal fun fraction(startValue: Float, endValue: Float, value: Float): Float {
                return (value - startValue) / (endValue - startValue)
            }
        }


        val viewDragCallback = object : ViewDragHelper.Callback() {
            var viewCapturedOriginalLeft: Int = 0
            var activePointerId: Int = View.NO_ID

            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                return activePointerId == View.NO_ID
            }

            override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
                isViewDragging = true

                this.activePointerId = activePointerId
                this.viewCapturedOriginalLeft = capturedChild.left

                capturedChild.parent?.let {
                    it.requestDisallowInterceptTouchEvent(true)
                }

                Timber.d("ViewCaptured")
            }

            override fun onViewReleased(child: View, xvel: Float, yvel: Float) {
                isViewDragging = false

                this.activePointerId = View.NO_ID

                val childWidth = child.getWidth()
                val targetLeft: Int
                var dismiss = false

                if (viewShouldDismissed(child, xvel)) {
                    targetLeft = if (child.getLeft() < viewCapturedOriginalLeft) {
                        viewCapturedOriginalLeft - childWidth
                    } else {
                        viewCapturedOriginalLeft + childWidth
                    }

                    dismiss = true
                } else {
                    // Else, reset back to the original left
                    targetLeft = viewCapturedOriginalLeft
                }

                if (dragHelper.settleCapturedViewAt(targetLeft, child.getTop())) {
                    ViewCompat.postOnAnimation(child, object : Runnable {
                        override fun run() {
                            if (dragHelper.continueSettling(true)) {
                                ViewCompat.postOnAnimation(child, this)
                            } else if (dismiss) {
                                dismissCallback?.invoke()
                            }
                        }
                    })
                } else if (dismiss) {
                    dismissCallback?.invoke()
                }
            }

            override fun getViewHorizontalDragRange(child: View): Int {
                return child.width
            }

            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                val min: Int = viewCapturedOriginalLeft
                val max: Int = min + child.width

                return left.clamp(min, max)
            }

            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                return child.top
            }

            private fun viewShouldDismissed(child: View, xvel: Float): Boolean {
                if (xvel != 0f) {
                    val isRtl = ViewCompat.getLayoutDirection(child) == ViewCompat.LAYOUT_DIRECTION_RTL
                    return if (isRtl) xvel < 0f else xvel > 0f
                } else {
                    val distance = child.left - viewCapturedOriginalLeft
                    val thresholdDistance = Math.round(child.width * DEFAULT_DRAG_DISMISS_THRESHOLD)
                    return Math.abs(distance) >= thresholdDistance
                }
            }

            override fun onViewPositionChanged(child: View, left: Int, top: Int, dx: Int, dy: Int) {
                val startAlphaDistance = viewCapturedOriginalLeft + child.width * DEFAULT_ALPHA_START_DISTANCE
                val endAlphaDistance = viewCapturedOriginalLeft + child.width * DEFAULT_ALPHA_END_DISTANCE

                if (left <= startAlphaDistance) {
                    child.alpha = 1f
                } else if (left >= endAlphaDistance) {
                    child.alpha = 0f
                } else {
                    // We're between the start and end distances
                    val distance = fraction(startAlphaDistance, endAlphaDistance, left.toFloat())
                    child.alpha = (1f - distance).clamp(0f, 1f)
                }
            }
        }

        val dragHelper: ViewDragHelper
        var dismissCallback: (() -> Unit)? = null
        var isViewDragging = false

        init {
            dragHelper = ViewDragHelper.create(this, viewDragCallback)
            clipChildren = false
            clipToPadding = false

            addView(target)
            customFrameLayoutParams(height = ViewGroup.LayoutParams.MATCH_PARENT)
        }

        override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
            var dispatchEventToHelper = interceptingEvents

            when (event.getActionMasked()) {
                MotionEvent.ACTION_DOWN -> {
                    interceptingEvents = isPointInChildBounds(target,
                            event.getX().toInt(), event.getY().toInt())

                    dispatchEventToHelper = interceptingEvents
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                    // Reset the ignore flag for next time
                    interceptingEvents = false
            }

            Timber.d("onIntercepting=$interceptingEvents")
            if (dispatchEventToHelper) {
                return dragHelper.shouldInterceptTouchEvent(event)
            }
            return false
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            Timber.d("interceptingOnTouch=$interceptingEvents")

            if (interceptingEvents) {
                dragHelper.processTouchEvent(event)
                return true
            } else {
                return false
            }
        }

        val tmpRect = Rect()
        var interceptingEvents = false

        /**
         * Check if a given point in the this coordinates are within the view bounds
         * of the given direct child view.
         *
         * @param child child view to test
         * @param x X coordinate to test
         * @param y Y coordinate to test
         *
         * @return true if the point is within the child view's bounds, false otherwise
         */
        fun isPointInChildBounds(child: View, x: Int, y: Int): Boolean {
            child.getHitRect(tmpRect)

            Timber.d("tmpRect: $tmpRect, x: $x, y: $y")
            try {
                return tmpRect.contains(x, y)
            } finally {
                tmpRect.setEmpty()
            }
        }
    }
}
