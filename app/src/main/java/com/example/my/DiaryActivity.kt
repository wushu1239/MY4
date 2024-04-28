package com.example.my

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.text.SimpleDateFormat
import java.util.*


class DiaryActivity : AppCompatActivity() {

    companion object {
        private const val SETTINGS_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyTheme()
        applyFont()

    }


    //테마 설정
    private fun applyTheme() {
        // 이전에 설정된 테마를 불러옵니다.
        val savedTheme = loadThemeFromSharedPreferences()
        // 선택된 테마에 따라 액티비티의 테마를 설정합니다.

        if (savedTheme == SettingsActivity.THEME_DARK) {
            setTheme(R.style.AppTheme_Dark)
        } else {
            setTheme(R.style.AppTheme_Light)
        }

        setContentView(R.layout.activity_diary)

        // 버튼 초기화 및 테마에 따라 이미지 설정
        initializeViews()
    }
    private fun applyFont() {
        val selectedFont = loadFontFromSharedPreferences()
        val typeface = getTypeface(selectedFont)
        setTypefaceForApp(typeface)
    }

    // 초기화 및 이미지설정
    private fun initializeViews() {
        val settingsButton = findViewById<Button>(R.id.settingsButton)
        val savedTheme = loadThemeFromSharedPreferences()
        if (savedTheme == SettingsActivity.THEME_DARK) {
            settingsButton.setBackgroundResource(R.drawable.setting_white)
        } else {
            settingsButton.setBackgroundResource(R.drawable.setting)
        }

        val calendarView = findViewById<MaterialCalendarView>(R.id.calendarView)




        calendarView.setOnDateChangedListener { _, date, _ ->
            val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date.date)
            showWriteDiaryDialog(dateString)
        }

        settingsButton.setOnClickListener {
            startActivityForResult(
                Intent(this, SettingsActivity::class.java),
                SETTINGS_REQUEST_CODE
            )
        }
    }

    // 액티비티가 설정이 변경된경우 껏다가 다시 켜주는 일을함
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_OK) {
            // 설정이 변경된 경우, 현재 액티비티를 종료한 후 다시 시작합니다.
            finish()
            startActivity(intent)
        }
    }

    // 일기 작성칸을 보여주는 함수
    private fun showWriteDiaryDialog(date: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.writediarydialog, null)
        dialogView.findViewById<TextView>(R.id.textViewDate).text = "Date: $date"

        val savedDiary = loadDiaryFromSharedPreferences(date)
        if (savedDiary != null) {
            dialogView.findViewById<EditText>(R.id.editTextDiary).setText(savedDiary)
        }

        // 다이얼로그에 폰트 적용
        val selectedFont = loadFontFromSharedPreferences()
        val typeface = getTypeface(selectedFont)
        setTypefaceForDialog(dialogView, typeface)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        dialogView.findViewById<Button>(R.id.btnSave).setOnClickListener {
            val diaryContent = dialogView.findViewById<EditText>(R.id.editTextDiary).text.toString()
            saveDiaryToSharedPreferences(date, diaryContent)
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            alertDialog.dismiss()
        }
    }

    // 다이얼로그에 폰트 적용하는 메서드
    private fun setTypefaceForDialog(dialogView: View, typeface: Typeface?) {
        val textViewDate = dialogView.findViewById<TextView>(R.id.textViewDate)
        textViewDate.typeface = typeface

        val editTextDiary = dialogView.findViewById<TextView>(R.id.editTextDiary)
        editTextDiary.typeface = typeface
    }


    //일기 저장
    private fun saveDiaryToSharedPreferences(date: String, content: String) {
        val sharedPreferences = getSharedPreferences("Diary", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(date, content)
        editor.apply()
    }


    //일기 불러오기
    private fun loadDiaryFromSharedPreferences(date: String): String? {
        val sharedPreferences = getSharedPreferences("Diary", Context.MODE_PRIVATE)
        return sharedPreferences.getString(date, null)
    }


    //테마 불러오기
    private fun loadThemeFromSharedPreferences(): String {
        val sharedPreferences =
            getSharedPreferences(SettingsActivity.PREF_SETTINGS, Context.MODE_PRIVATE)
        return sharedPreferences.getString(SettingsActivity.KEY_THEME, SettingsActivity.THEME_LIGHT)
            ?: SettingsActivity.THEME_LIGHT
    }
    private fun getTypeface(selectedFont: String): Typeface? {
        return when (selectedFont) {
            "코트라 손글씨" -> Typeface.createFromAsset(assets, "kotra_songeulssi.otf")
            "교보문고 우선우" -> Typeface.createFromAsset(assets, "kyobo_hand_writing_wsa.ttf")
            else -> null
        }
    }

    private fun setTypefaceForApp(typeface: Typeface?) {
        val rootView = findViewById<View>(android.R.id.content)
        setFontForViewGroup(rootView as ViewGroup, typeface)
    }


    private fun loadFontFromSharedPreferences(): String {
        val sharedPreferences = getSharedPreferences(SettingsActivity.PREF_SETTINGS, Context.MODE_PRIVATE)
        return sharedPreferences.getString(SettingsActivity.KEY_FONT, "Default") ?: "Default"
    }

    private fun setFontForViewGroup(viewGroup: ViewGroup, typeface: Typeface?) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is TextView) {
                child.typeface = typeface
            } else if (child is ViewGroup) {
                setFontForViewGroup(child, typeface)
            }
        }
    }



}