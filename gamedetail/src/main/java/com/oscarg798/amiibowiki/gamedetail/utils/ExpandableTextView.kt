/*
 * Copyright 2020 Oscar David Gallon Rosero
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package com.oscarg798.amiibowiki.gamedetail.utils

import android.animation.LayoutTransition
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatTextView
import com.oscarg798.amiibowiki.gamedetail.R

/**
 * Inspired from
 * https://medium.com/@yuriyskul/expandable-textview-with-layouttransition-part-1-b506681e78e7
 */
class ExpandableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr), View.OnClickListener {

    private var currentAnimationState = IDLE_ANIMATION_STATE
    private var isCollapsed = INITIAL_IS_COLLAPSED

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setup()
    }

    private fun setup() {
        setOnClickListener(this)
        maxLines = MAX_LINES_COLLAPSED
        setupCollapseMode()
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)

        viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {

                override fun onGlobalLayout() {
                    if (isTextUnlimited()) {
                        if (canBeCollapsed()) {
                            setupExpandedMode()
                        } else {
                            setupCollapseMode()
                        }
                    } else {
                        if (isTrimmedWithLimitLines()) {
                            setupExpandedMode()
                        } else {
                            setupCollapseMode()
                        }
                    }
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })

        applyLayoutTransition()
    }

    private fun setupCollapseMode() {
        isClickable = true
        ellipsize = TextUtils.TruncateAt.END
        setupCollapsedDrawable()
    }

    private fun setupExpandedMode() {
        isClickable = false
        ellipsize = null
    }

    private fun isTextUnlimited(): Boolean {
        return maxLines == Int.MAX_VALUE
    }

    private fun canBeCollapsed(): Boolean {
        return lineCount <= MAX_LINES_COLLAPSED
    }

    private fun isTrimmedWithLimitLines(): Boolean {
        return lineCount <= maxLines
    }

    override fun onClick(view: View) {
        if (isRunning()) {
            val parentGroup = parent as ViewGroup
            (parentGroup).setLayoutTransition(parentGroup.layoutTransition)
        }

        if (isCollapsed) {
            currentAnimationState = EXPANDING_ANIMATION_STATE
            maxLines = Integer.MAX_VALUE
            setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                0,
                R.drawable.ic_collapse
            )
        } else {
            currentAnimationState = COLLAPSING_ANIMATION_STATE
            maxLines = MAX_LINES_COLLAPSED
            setupCollapsedDrawable()
        }

        isCollapsed = !isCollapsed
    }

    private fun setupCollapsedDrawable() {
        setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            0,
            R.drawable.ic_expand
        )
    }

    private fun applyLayoutTransition() {
        val parentGroup = parent as? ViewGroup ?: return
        val transition = LayoutTransition()
        transition.setDuration(TRANSITION_ANIMATION)
        transition.enableTransitionType(LayoutTransition.CHANGING)
        parentGroup.setLayoutTransition(transition)
    }

    private fun isIdle(): Boolean {
        return currentAnimationState == IDLE_ANIMATION_STATE
    }

    private fun isRunning(): Boolean {
        return !isIdle()
    }
}

private const val ELLIPSIZE_TEXT = "Read more"
private const val TRANSITION_ANIMATION = 300L
private const val IDLE_ANIMATION_STATE = 1
private const val EXPANDING_ANIMATION_STATE = 2
private const val COLLAPSING_ANIMATION_STATE = 3
private const val MAX_LINES_COLLAPSED = 4
private const val INITIAL_IS_COLLAPSED = true
