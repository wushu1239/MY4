package com.example.my
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeSpinner: Spinner
    private lateinit var fontSpinner: Spinner
    private lateinit var btnSave: Button

    private val themeOptions = arrayOf(THEME_LIGHT, THEME_DARK)
    private val fontOptions = arrayOf("시스템 폰트", "코트라 손글씨", "교보문고 우선우")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyTheme() // 액티비티가 생성될 때 테마를 적용합니다.
        setContentView(R.layout.activity_settings)

        themeSpinner = findViewById(R.id.themeSpinner)
        fontSpinner = findViewById(R.id.fontSpinner)
        btnSave = findViewById(R.id.btnSave)

        val savedTheme = loadThemeFromSharedPreferences()

        val themeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, themeOptions)
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        themeSpinner.adapter = themeAdapter
        val savedThemeIndex = themeOptions.indexOf(savedTheme)
        if (savedThemeIndex != -1) {
            themeSpinner.setSelection(savedThemeIndex)
        }

        val fontAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fontOptions)
        fontAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fontSpinner.adapter = fontAdapter
        val savedFontIndex = fontOptions.indexOf(loadFontFromSharedPreferences())
        if (savedFontIndex != -1) {
            fontSpinner.setSelection(savedFontIndex)
        }

        btnSave.setOnClickListener {
            saveThemeAndFontAndNotify()
            recreate()
        }

        themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedTheme = themeOptions[position]
                saveThemeToSharedPreferences(selectedTheme)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        fontSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedFont = fontOptions[position]
                saveFontToSharedPreferences(selectedFont)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }


    override fun onResume() {
        super.onResume()
        applyFont() // 액티비티가 다시 시작될 때 폰트를 적용합니다.
    }
    private fun applyFont() {
        val selectedFont = loadFontFromSharedPreferences()
        val typeface = getTypeface(selectedFont)
        setTypefaceForApp(typeface)
    }

    private fun getTypeface(selectedFont: String): Typeface? {
        return when (selectedFont) {
            "코트라 손글씨" -> Typeface.createFromAsset(assets, "kotra_songeulssi.otf")
            "교보문고 우선우" -> Typeface.createFromAsset(assets, "kyobo_hand_writing_wsa.ttf")
            else -> null
        }
    }

    private fun saveFontToSharedPreferences(font: String) {
        val sharedPreferences = getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_FONT, font)
        editor.apply()
    }

    private fun setTypefaceForApp(typeface: Typeface?) {
        val rootView = findViewById<View>(android.R.id.content)
        setFontForViewGroup(rootView as ViewGroup, typeface)
    }


    private fun loadFontFromSharedPreferences(): String {
        val sharedPreferences = getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_FONT, "Default") ?: "Default"
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


    private fun saveThemeAndFontAndNotify() {
        val intent = Intent(this, DiaryActivity::class.java)
        intent.putExtra(EXTRA_THEME_CHANGED, true)
        intent.putExtra(EXTRA_FONT_CHANGED, true) // 폰트 변경 사항을 알립니다.
        intent.putExtra(KEY_FONT, loadFontFromSharedPreferences()) // 선택한 폰트의 이름을 전달

        setResult(RESULT_OK, intent) // 설정이 변경되었음을 알립니다.
        finish()
    }

    private fun applyTheme() {
        val savedTheme = loadThemeFromSharedPreferences()
        val newTheme = if (savedTheme == THEME_DARK) R.style.AppTheme_Dark else R.style.AppTheme_Light
        setTheme(newTheme)
    }
    private fun saveThemeToSharedPreferences(theme: String) {
        val sharedPreferences = getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_THEME, theme)
        editor.apply()
    }

    private fun loadThemeFromSharedPreferences(): String {
        val sharedPreferences = getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_THEME, THEME_LIGHT) ?: THEME_LIGHT
    }

    companion object {
        const val EXTRA_THEME_CHANGED = "ThemeChanged"
        const val EXTRA_FONT_CHANGED = "FontChanged"
        const val PREF_SETTINGS = "Settings"
        const val KEY_THEME = "Theme"
        const val KEY_FONT = "Font"
        const val THEME_LIGHT = "Light"
        const val THEME_DARK = "Dark"
    }
}