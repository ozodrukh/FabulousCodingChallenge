package codetail.io.fabulouscoddingchallenge

import android.app.Activity
import android.support.animation.SpringAnimation
import android.support.design.widget.FloatingActionButton
import android.view.Gravity
import android.view.View
import codetail.io.fabulouscoddingchallenge.ext.customFrameLayoutParams
import codetail.io.fabulouscoddingchallenge.ext.dp
import codetail.io.fabulouscoddingchallenge.ext.onceMeasured

/**
 * created at 7/21/17
 *
 * @author Ozodrukh
 * @version 1.0
 */
class BubblesSwapOnScroll(context: Activity, secondaryView: FloatingActionButton)
    : ViewSwitcherOnScrollPositionChange() {

    val primaryView = FloatingViewHelper.make(R.layout.floation_ritual_bubble_view, context)
    var primaryViewShowing = true
    var primaryViewAnimation: SpringAnimation? = null
    var primaryViewDismissed = false

    val secondaryView = secondaryView

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

        primaryView.onDismissed = {
            primaryViewShowing = false
            primaryViewDismissed = true
            secondaryView.show()
        }

        primaryView.show()
    }

    override fun onViewFlipping(showSecondaryView: Boolean) {
        if (primaryViewDismissed) {
            return
        }

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
                    secondaryView.show()
                }

                primaryViewAnimation = animation
                primaryViewShowing = false
            }
        } else {
            secondaryView.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
                override fun onHidden(fab: FloatingActionButton) {
                    if (!primaryView.showing) {
                        primaryView.show()
                        primaryViewShowing = true
                    }
                }
            })
        }
    }

    override fun createSecondaryView() {

    }

}