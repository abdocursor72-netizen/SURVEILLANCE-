package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("sf_surveillance_prefs", Context.MODE_PRIVATE)

    var defaultObserverName: String
        get() = prefs.getString(KEY_OBSERVER_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_OBSERVER_NAME, value).apply()

    var exportLanguage: String
        get() = prefs.getString(KEY_EXPORT_LANG, "الفرنسية") ?: "الفرنسية"
        set(value) = prefs.edit().putString(KEY_EXPORT_LANG, value).apply()

    var themeMode: String
        get() = prefs.getString(KEY_THEME_MODE, "تلقائي") ?: "تلقائي"
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value).apply()

    var customSamplesJson: String
        get() = prefs.getString(KEY_CUSTOM_SAMPLES, "") ?: ""
        set(value) = prefs.edit().putString(KEY_CUSTOM_SAMPLES, value).apply()

    var isAutoRefreshEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTO_REFRESH, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_REFRESH, value).apply()

    var autoRefreshIntervalSeconds: Int
        get() = prefs.getInt(KEY_AUTO_REFRESH_INTERVAL, 10)
        set(value) = prefs.edit().putInt(KEY_AUTO_REFRESH_INTERVAL, value).apply()

    var updateServerUrl: String
        get() = prefs.getString(KEY_UPDATE_SERVER_URL, DEFAULT_UPDATE_URL) ?: DEFAULT_UPDATE_URL
        set(value) = prefs.edit().putString(KEY_UPDATE_SERVER_URL, value).apply()

    companion object {
        const val DEVELOPER_NAME = "by ABDALKAYOUM MOUSAID."
        const val APP_TITLE = "surveillance-maladie"
        const val APP_VERSION = "1.11"
        const val CURRENT_VERSION_CODE = 15
        const val DEFAULT_UPDATE_URL = "https://raw.githubusercontent.com/abdocursor72/SF-Surveillance/main/version.json"

        private const val KEY_OBSERVER_NAME = "pref_observer_name"
        private const val KEY_EXPORT_LANG = "pref_export_lang"
        private const val KEY_THEME_MODE = "pref_theme_mode"
        private const val KEY_CUSTOM_SAMPLES = "pref_custom_samples"
        private const val KEY_AUTO_REFRESH = "pref_auto_refresh"
        private const val KEY_AUTO_REFRESH_INTERVAL = "pref_auto_refresh_interval"
        private const val KEY_UPDATE_SERVER_URL = "pref_update_server_url"
    }
}
