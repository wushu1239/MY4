package com.example.my

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mainLayout: View
    private lateinit var settingsButton: Button
    private lateinit var selectedOption: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainLayout = findViewById(R.id.mainLayout)
        settingsButton = findViewById(R.id.settingsButton)

        // 앱 시작 시 저장된 이미지 옵션을 불러와 배경 설정
        selectedOption = loadStartScreenOption() ?: "Image Option 1"
        applyStartScreen(selectedOption)

        // 배경화면 설정 버튼 클릭 이벤트 설정
        settingsButton.setOnClickListener {
            showBackgroundSelectionDialog()
        }

        // 메인 레이아웃 클릭 이벤트 설정
        mainLayout.setOnClickListener {
            // DiaryActivity로 이동
            val intent = Intent(this, DiaryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showBackgroundSelectionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.start_screen_selection_dialog, null)
        val imageOptions = Array(5) { index ->
            dialogView.findViewById<View>(resources.getIdentifier("imageOption${index + 1}", "id", packageName))
        }

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("배경화면 변경")

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        imageOptions.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                selectImageOption("Image Option ${index + 1}")
                alertDialog.dismiss()
            }
        }
    }

    private fun selectImageOption(option: String) {
        selectedOption = option
        saveStartScreenOption(option)
        applyStartScreen(option)
        applyButtonImage(option)
    }

    private fun applyStartScreen(option: String) {
        val drawableId = resources.getIdentifier("start_image${option.last()}", "drawable", packageName)
        if (drawableId != 0) {
            mainLayout.setBackgroundResource(drawableId)
        } else {
            // 이미지가 로드되지 않았을 때의 처리
        }
    }

    private fun applyButtonImage(option: String) {
        val buttonDrawableId = when {
            option == "Image Option 4" -> R.drawable.setting
            else -> R.drawable.setting_white
        }
        settingsButton.setBackgroundResource(buttonDrawableId)
    }

    private fun loadStartScreenOption(): String? {
        val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("StartScreenOption", null)
    }

    private fun saveStartScreenOption(option: String) {
        val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("StartScreenOption", option)
        editor.apply()
    }
}
