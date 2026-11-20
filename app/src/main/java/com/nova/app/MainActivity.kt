package com.nova.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.core.view.WindowCompat
import com.nova.app.data.AppSettings
import com.nova.app.data.Locales
import com.nova.app.ui.NovaApp
import com.nova.app.ui.Strings
import com.nova.app.ui.settings.AppLanguage
import com.nova.app.ui.settings.AppThemeMode
import com.nova.app.ui.settings.FontScaleSteps
import com.nova.app.ui.theme.NovaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        setContent {
            val baseContext = LocalContext.current
            val systemDark = isSystemInDarkTheme()

            var language by remember {
                mutableStateOf(AppLanguage.fromTag(AppSettings.languageTag(baseContext)))
            }
            var themeMode by remember {
                mutableStateOf(AppThemeMode.fromKey(AppSettings.themeMode(baseContext)))
            }
            var fontScaleIndex by remember {
                mutableStateOf(AppSettings.fontScaleIndex(baseContext))
            }
            var followSystemFont by remember {
                mutableStateOf(AppSettings.followSystemFont(baseContext))
            }
            var autoCollapseThinking by remember {
                mutableStateOf(AppSettings.autoCollapseThinking(baseContext))
            }

            val darkTheme = when (themeMode) {
                AppThemeMode.System -> systemDark
                AppThemeMode.Light -> false
                AppThemeMode.Dark -> true
            }
            val systemFontScale = LocalDensity.current.fontScale
            val fontScale = if (followSystemFont) {
                systemFontScale
            } else {
                FontScaleSteps[fontScaleIndex.coerceIn(FontScaleSteps.indices)]
            }
            val strings = Strings(Locales.wrap(baseContext))

            SideEffect {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }

            NovaTheme(darkTheme = darkTheme, fontScale = fontScale, strings = strings) {
                NovaApp(
                    themeMode = themeMode,
                    onThemeModeChange = {
                        themeMode = it
                        AppSettings.setThemeMode(baseContext, it.key)
                    },
                    language = language,
                    onLanguageChange = { selected ->
                        language = selected
                        AppSettings.setLanguageTag(baseContext, selected.tag)
                        Locales.apply(selected.tag)
                    },
                    fontScaleIndex = fontScaleIndex,
                    followSystemFont = followSystemFont,
                    onFontScaleChange = {
                        fontScaleIndex = it
                        AppSettings.setFontScaleIndex(baseContext, it)
                    },
                    onFollowSystemFontChange = {
                        followSystemFont = it
                        AppSettings.setFollowSystemFont(baseContext, it)
                    },
                    autoCollapseThinking = autoCollapseThinking,
                    onAutoCollapseThinkingChange = {
                        autoCollapseThinking = it
                        AppSettings.setAutoCollapseThinking(baseContext, it)
                    },
                    onLanguageNeedsRestart = { restartActivity() },
                )
            }
        }
    }

    private fun restartActivity() {
        startActivity(
            Intent(this, MainActivity::class.java).addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK,
            ),
        )
        finish()
    }
}
