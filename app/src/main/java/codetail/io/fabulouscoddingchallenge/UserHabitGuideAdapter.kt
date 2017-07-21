package codetail.io.fabulouscoddingchallenge

import android.support.v4.graphics.ColorUtils.HSLToColor
import android.support.v7.appcompat.R.style.TextAppearance_AppCompat_Large
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import codetail.io.fabulouscoddingchallenge.ext.customMarginLayoutParams
import codetail.io.fabulouscoddingchallenge.ext.dp
import codetail.io.fabulouscoddingchallenge.ext.textAppearance

/**
 * created at 7/21/17
 *
 * @author Ozodrukh
 * @version 1.0
 */

class UserHabitGuideAdapter : RecyclerView.Adapter<UserHabitGuideAdapter.SimpleViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val fakeHeight = (parent.resources.displayMetrics.density * 220).toInt()

        return SimpleViewHolder(CardView(parent.context).apply {
            layoutParams = customMarginLayoutParams(height = fakeHeight,
                    bottomMargin = context.dp(8f))

            addView(TextView(context).apply {
                gravity = Gravity.CENTER
                textAppearance(TextAppearance_AppCompat_Large)
            })
        })
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = 100

    class SimpleViewHolder(itemView: CardView) : RecyclerView.ViewHolder(itemView) {
        val textView = itemView.getChildAt(0) as TextView

        fun bind(position: Int) {
            itemView.setBackgroundColor(HSLToColor(floatArrayOf(position + 1f, 0.6F, 0.6f)))
            textView.text = position.toString()
        }
    }
}
