package cloud.sud.instatram

import android.content.Context
import android.preference.PreferenceManager

class MyPreferences(context: Context?) {
    companion object {
        private const val DARK_STATUS = "DARK STATUS"
        private const val LANGUAGE ="language"
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var darkMode = preferences.getInt(DARK_STATUS, 0)
        set(value) = preferences.edit().putInt(DARK_STATUS, value).apply()


    var language = preferences.getInt(LANGUAGE, 0)
        set(value) = preferences.edit().putInt(LANGUAGE, value).apply()
}