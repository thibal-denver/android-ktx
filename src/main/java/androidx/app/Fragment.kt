/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.app

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView


fun Fragment.addNotificationHeightPadding(view: View?) {
    view?.setPadding(0, getStatusBarHeight(), 0, 0)
}

fun Fragment.resetHeightPadding(view: View?) {
    view?.setPadding(0, 0, 0, 0)
}

fun Fragment.getStatusBarHeight(): Int {
    // A method to find height of the status bar
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}

fun Fragment.initBottomSheet(bottomSheet: View, animationDuration: Long) {
    val viewTreeObserver = bottomSheet.viewTreeObserver
    viewTreeObserver
        .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                bottomSheet.animate()
                    .translationY(bottomSheet.height.toFloat())
                    .setDuration(0)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            bottomSheet.visibility = View.GONE
                        }
                    })
                bottomSheet.viewTreeObserver
                    .removeOnGlobalLayoutListener(this)
            }
        })
    bottomSheet.animate().duration = animationDuration
}

fun Fragment.showBottomSheet(bottomSheet: View, animationDuration: Long) {
    bottomSheet.visibility = View.VISIBLE
    bottomSheet.animate()
        .translationY(0f)
        .setDuration(animationDuration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                bottomSheet.visibility = View.VISIBLE
            }
        })
}

fun Fragment.dismissBottomSheet(bottomSheet: View, animationDuration: Long) {
    bottomSheet.animate()
        .translationY(bottomSheet.height.toFloat())
        .setDuration(animationDuration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                bottomSheet.visibility = View.GONE
            }
        })
}

fun Fragment.getScreenWidth(context: Context): Int {
    val dm = DisplayMetrics()
    val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
    windowManager.defaultDisplay.getMetrics(dm)
    return dm.widthPixels
}

fun Fragment.setCursorDrawableColor(editText: EditText, color: Int) {
    try {
        val fCursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
        fCursorDrawableRes.isAccessible = true
        val mCursorDrawableRes = fCursorDrawableRes.getInt(editText)
        val fEditor = TextView::class.java.getDeclaredField("mEditor")
        fEditor.isAccessible = true
        val editor = fEditor.get(editText)
        val clazz = editor.javaClass
        val fCursorDrawable = clazz.getDeclaredField("mCursorDrawable")
        fCursorDrawable.isAccessible = true

        val drawables: Array<Drawable?> =
            Array(2, { context?.let {ContextCompat.getDrawable(it, mCursorDrawableRes) }})
        drawables[0]?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        drawables[1]?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        fCursorDrawable.set(editor, drawables)
    } catch (ignored: Throwable) {
        Log.e("CursorColor", "issue tint cursor: ", ignored)
    }
}


