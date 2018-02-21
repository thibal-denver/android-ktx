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
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.kotlin.R

fun AppCompatActivity.addNotificationHeightPadding(view: View) {
    view.setPadding(0, getStatusBarHeight(), 0, 0)
}

fun AppCompatActivity.getStatusBarHeight(): Int {
    // A method to find height of the status bar
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}

fun AppCompatActivity.hideKeyBoard() {
    val viewFocused = currentFocus
    if (viewFocused != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(viewFocused.windowToken, 0)
    }
}

fun AppCompatActivity.initRightView(view: View, animationDuration: Long) {
    val viewTreeObserver = view.viewTreeObserver
    viewTreeObserver
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    view.animate()
                            .translationX(view.width.toFloat())
                            .setDuration(0)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    super.onAnimationEnd(animation)
                                    view.visibility = View.GONE
                                }
                            })
                    view.viewTreeObserver
                            .removeOnGlobalLayoutListener(this)
                }
            })
    view.animate().duration = animationDuration
}

fun AppCompatActivity.showRightView(view: View, animationDuration: Long) {
    view.visibility = View.VISIBLE
    view.animate()
            .translationX(0f)
            .setDuration(animationDuration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    view.visibility = View.VISIBLE
                }
            })
}

fun AppCompatActivity.dismissRightView(view: View, animationDuration: Long) {
    view.animate()
            .translationX(view.width.toFloat())
            .setDuration(animationDuration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    view.visibility = View.GONE
                }
            })
}

fun AppCompatActivity.configureScreenOrientation(orientation: Int) {
    requestedOrientation = orientation
}

fun AppCompatActivity.exitModalWay() {
    overridePendingTransition(R.anim.nothing, R.anim.slide_out_bottom)
}
