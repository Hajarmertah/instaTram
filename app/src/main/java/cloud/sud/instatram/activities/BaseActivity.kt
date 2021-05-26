package cloud.sud.instatram.activities

import android.content.res.Configuration
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import cloud.sud.instatram.MyPreferences
import cloud.sud.instatram.R
import java.util.*

open class BaseActivity: AppCompatActivity() {

    fun themeDialog() {
        val light:String =getString(R.string.light)
        val dark:String =getString(R.string.dark)
        val systemDefault:String =getString(R.string.system_default)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.theme))
        val styles = arrayOf(light,dark,systemDefault)
        val checkedItem = MyPreferences(this).darkMode


        builder.setSingleChoiceItems(styles, checkedItem) { dialog, which ->
            when (which) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    delegate.applyDayNight()
                    MyPreferences(this).darkMode = 0
                    dialog.dismiss()
                }
                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    delegate.applyDayNight()
                    MyPreferences(this).darkMode = 1
                    dialog.dismiss()
                }
                2 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    delegate.applyDayNight()
                    MyPreferences(this).darkMode = 2

                    dialog.dismiss()
                }
            }

        }
        val dialog = builder.create()
        dialog.show()
    }

    fun checkTheme() {
        when (MyPreferences(this).darkMode) {
            0 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                delegate.applyDayNight()
            }
            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                delegate.applyDayNight()
            }
            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                delegate.applyDayNight()
            }
        }
    }


    fun languageDialog() {
        val english = getString(R.string.english)
        val spanish = getString(R.string.spanish)
        val languages = arrayOf(english, spanish)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.choose_language_text))
        val checkedItem = MyPreferences(this).language

        builder.setSingleChoiceItems(languages, checkedItem) { dialog, which ->
            when (which) {
                0 -> {
                    setLocate("en")
                    recreate()
                    MyPreferences(this).language = 0
                    dialog.dismiss()
                }
                1 -> {
                    setLocate("es")
                    recreate()
                    MyPreferences(this).language = 1
                    dialog.dismiss()
                }
            }
        }
        val dialog = builder.create()
        dialog.show()
    }


    fun setLocate(lang: String) {
        val locale= Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale =locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

    }
    fun loadLocate() {
        when (MyPreferences(this).language) {
            0 -> {
                setLocate("en")
            }
            1 -> {
                setLocate("es")
            }
        }
    }


}