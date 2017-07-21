package codetail.io.fabulouscoddingchallenge.ext

import android.content.Context
import android.support.annotation.ColorRes
import android.support.v4.content.res.ResourcesCompat
import android.view.View

/**
 * @author Ozodrukh
 * @version 1.0
 */
fun View.colorOf(@ColorRes id: Int): Int {
    return ResourcesCompat.getColor(resources, id, context.theme);
}

fun Context.dp(value: Float): Int {
    return (resources.displayMetrics.density * value).toInt()
}