package com.example.scrollnumber.lib

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.animation.BounceInterpolator
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.abs

/**
 *  Created by wangyh on 2023/08/08 16:00:01
 *
 */
class ScrollNumberTextView(context: Context, attrs: AttributeSet?) : AppCompatTextView(context, attrs) {
  //当前text
  private var text: String = ""
  private var targetNum = 0
  private var nextTargetNum = 0

  //总滚动距离数组
  private var mScrollY: Int = 0

  private var animDuration: Long = 300

  //滚动完成判断
  private var reachTarget: Boolean = false
  private var mPaint: Paint = paint

  //第一次绘制
  private var firstIn = true
  private var withAnim = false

  //滚动中
  private var animating = false

  //基准线
  private var mBaseline = 0
  private var mMeasuredHeight = 0

  init {

  }

  //开始滚动
  fun start(shouldAnim: Boolean) {
    text = getText().toString()
    if (text.length == 1 && shouldAnim && isIntChar(text[0])) {
      if (getTargetNum() == targetNum) {
        if (!animating) {
          withAnim(false)
          invalidate()
        }
        return
      }
      if (animating) {
        nextTargetNum = getTargetNum()
      } else {
        targetNum = getTargetNum()
        nextTargetNum = targetNum
        withAnim(true)
        reachTarget = false
        startAnimatorLoop()
      }
    } else {
      withAnim(false)
      invalidate()
    }
  }

  private fun withAnim(withAnim: Boolean) {
    this.withAnim = withAnim
  }

  private fun isIntChar(char: Char): Boolean {
    return char in '0'..'9'
  }

  override fun onDraw(canvas: Canvas) {
    if (!withAnim) {
      super.onDraw(canvas)
      return
    }
    if (firstIn) {
      firstIn = false
      super.onDraw(canvas)
      mPaint = paint
      val fontMetrics = mPaint.fontMetricsInt
      mMeasuredHeight = measuredHeight
      mBaseline = (mMeasuredHeight - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top
      mScrollY = mBaseline
      drawNumber(canvas)
    } else {
      drawNumber(canvas)
    }
  }

  //绘制
  private fun drawNumber(canvas: Canvas) {
    for (i in 0..9) {
      //计算偏移量和绘制判断逻辑
      if (i == 0) {
        if (reachTarget) {
          withAnim = false
          canvas.drawText(targetNum.toString(), 0f, mBaseline.toFloat(), mPaint)
          break
        }
      }
      if (!reachTarget && animating) {
        canvas.drawText(i.toString(), 0f, (-mScrollY + (i + 2) * mBaseline).toFloat(), mPaint)
      }
    }
  }

  private fun getTargetNum(): Int {
    return text[0].code - '0'.code
  }

  fun destroy() {
    stopAnimatorLoop()
  }

  private var valueAnimator: ValueAnimator? = null
  private fun stopAnimatorLoop() {
    valueAnimator?.removeAllUpdateListeners()
    valueAnimator?.cancel()
  }

  private fun startAnimatorLoop() {
    if (mBaseline <= 0) return
    stopAnimatorLoop()
    animating = true
    valueAnimator?.removeAllUpdateListeners()
    valueAnimator = ValueAnimator.ofInt(mScrollY, (targetNum + 1) * mBaseline).apply {
      addUpdateListener(animatorUpdateListener)
      addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
          animating = false
          if (targetNum != nextTargetNum) {
            targetNum = nextTargetNum
            startAnimatorLoop()
          } else {
            if (abs(mScrollY - (targetNum + 1) * mBaseline) < 10) {
              reachTarget = true
            }
            invalidate()
          }
        }

        override fun onAnimationCancel(animation: Animator) {
          animating = false
          invalidate()
        }
      })
      duration = animDuration
      interpolator = BounceInterpolator()
      start()
    }
  }

  private val animatorUpdateListener = ValueAnimator.AnimatorUpdateListener {
    if (animating) {
      mScrollY = it.animatedValue as Int
      invalidate()
    }
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    destroy()
  }
}