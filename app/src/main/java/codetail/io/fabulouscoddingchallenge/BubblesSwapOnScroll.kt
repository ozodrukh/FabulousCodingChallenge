package codetail.io.fabulouscoddingchallenge

import android.app.Activity
import android.support.animation.SpringAnimation
import android.view.Gravity
import codetail.io.fabulouscoddingchallenge.ext.customFrameLayoutParams
import codetail.io.fabulouscoddingchallenge.ext.dp
import codetail.io.fabulouscoddingchallenge.ext.onceMeasured

/**
 * created at 7/21/17
 *
 * @author Ozodrukh
 * @version 1.0
 */
class BubblesSwapOnScroll(context: Activity) : ViewSwitcherOnScrollPositionChange() {
    val primaryView = FloatingViewHelper.make(R.layout.floation_ritual_bubble_view, context)
    var primaryViewShowing = true
    var primaryViewAnimation: SpringAnimation? = null

    init {
        primaryView.target.customFrameLayoutParams(
                gravity = Gravity.BOTTOM,
                leftMargin = context.dp(16f),
                rightMargin = context.dp(16f),
                bottomMargin = context.dp(16f)
        )

        primaryView.onAttached = { view, parent ->
            view.onceMeasured {
                if (primaryViewAnimation != null) {
                    primaryViewAnimation?.cancel()
                    primaryViewAnimation = null
                }

                val height = (parent.bottom - view.bottom).toFloat()
                view.translationY = height

                val animation = SpringAnimation(view, SpringAnimation.TRANSLATION_Y)
                animation.animateToFinalPosition(0f)

                primaryViewAnimation = animation
            }
        }

        primaryView.show()
    }

    override fun onViewFlipping(showSecondaryView: Boolean) {
        if (showSecondaryView) {
            if (primaryViewShowing) {
                if (primaryViewAnimation != null) {
                    primaryViewAnimation?.cancel()
                    primaryViewAnimation = null
                }

                val height = (primaryView.parent.bottom - primaryView.target.bottom).toFloat() +
                            primaryView.target.height

                val animation = SpringAnimation(primaryView.target, SpringAnimation.TRANSLATION_Y)
                animation.animateToFinalPosition(height)
                animation.addEndListener { _, _, _, _ ->
                    primaryView.dismiss()
                }

                primaryViewAnimation = animation
                primaryViewShowing = false
            }
        } else {
            if (!primaryView.showing) {
                primaryView.show()
                primaryViewShowing = true
            }
        }
    }

    override fun createSecondaryView() {

    }

}