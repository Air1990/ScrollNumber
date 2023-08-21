package com.example.scrollnumber

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.scrollnumber.lib.MultiScrollNumberTextView

class TestActivity : AppCompatActivity() {
  private val scrollNumberTv by lazy { findViewById<MultiScrollNumberTextView>(R.id.scroll_number_tv) }
  private val changeNumberBt by lazy { findViewById<Button>(R.id.change_number_bt) }
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_test)
    changeText()
    changeNumberBt.setOnClickListener {
      changeText()
    }
  }

  private fun changeText() {
    scrollNumberTv.setText("$${System.currentTimeMillis() % 1000000}", true)
  }
}