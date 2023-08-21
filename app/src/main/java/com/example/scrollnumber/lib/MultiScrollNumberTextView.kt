package com.example.scrollnumber.lib

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout

/**
 *  Created by wangyh on 2023/08/09 11:26:31
 *
 */
class MultiScrollNumberTextView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
  private val scrollNumber = mutableListOf<ScrollNumberTextView>()
  var textSize = 30f
  var textColor = Color.parseColor("#000000")
  var textBold = false
  var currentText = ""

  init {
    orientation = HORIZONTAL
  }

  fun setText(targetText: String, withAnim: Boolean) {
    if (targetText.isEmpty()) return
    if (targetText.length > scrollNumber.size) {
      for (i in scrollNumber.size until targetText.length) {
        val number = ScrollNumberTextView(context, null)
        scrollNumber.add(0, number)
        addView(number, 0)
      }
    } else if (targetText.length < scrollNumber.size) {
      for (i in targetText.length until scrollNumber.size) {
        val first = scrollNumber.removeFirstOrNull()
        if (first != null) {
          removeView(first)
        }
      }
    }
    for (i in targetText.length - 1 downTo 0) {
      setTextStyle(scrollNumber[i])
      scrollNumber[i].text = targetText[i].toString()
      scrollNumber[i].start(withAnim)
    }
    currentText = targetText
  }

  private fun setTextStyle(number: ScrollNumberTextView) {
    number.textSize = textSize
    number.setTextColor(textColor)
  }
}