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

    var exportQuality: String
        get() = prefs.getString(KEY_EXPORT_QUALITY, "عالية") ?: "عالية"
        set(value) = prefs.edit().putString(KEY_EXPORT_QUALITY, value).apply()

    var exportIncludeViruses: Boolean
        get() = prefs.getBoolean(KEY_INCLUDE_VIRUSES, true)
        set(value) = prefs.edit().putBoolean(KEY_INCLUDE_VIRUSES, value).apply()

    var exportIncludePests: Boolean
        get() = prefs.getBoolean(KEY_INCLUDE_PESTS, true)
        set(value) = prefs.edit().putBoolean(KEY_INCLUDE_PESTS, value).apply()

    var exportIncludeBeneficials: Boolean
        get() = prefs.getBoolean(KEY_INCLUDE_BENEFICIALS, true)
        set(value) = prefs.edit().putBoolean(KEY_INCLUDE_BENEFICIALS, value).apply()

    var exportIncludeSpecialNotes: Boolean
        get() = prefs.getBoolean(KEY_INCLUDE_NOTES, true)
        set(value) = prefs.edit().putBoolean(KEY_INCLUDE_NOTES, value).apply()

    var exportIncludeCropPhoto: Boolean
        get() = prefs.getBoolean(KEY_INCLUDE_PHOTO, true)
        set(value) = prefs.edit().putBoolean(KEY_INCLUDE_PHOTO, value).apply()

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

    var whatsappContactNumber: String
        get() = prefs.getString(KEY_WHATSAPP_NUMBER, "") ?: ""
        set(value) = prefs.edit().putString(KEY_WHATSAPP_NUMBER, value).apply()

    var whatsappGroupUrl: String
        get() = prefs.getString(KEY_WHATSAPP_GROUP_URL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_WHATSAPP_GROUP_URL, value).apply()

    var isNotificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, value).apply()

    var notifyOnAppUpdate: Boolean
        get() = prefs.getBoolean(KEY_NOTIFY_APP_UPDATE, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFY_APP_UPDATE, value).apply()

    var notifyOnBackupReminder: Boolean
        get() = prefs.getBoolean(KEY_NOTIFY_BACKUP_REMINDER, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFY_BACKUP_REMINDER, value).apply()

    var notifyOnDiseaseThreshold: Boolean
        get() = prefs.getBoolean(KEY_NOTIFY_DISEASE_THRESHOLD, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFY_DISEASE_THRESHOLD, value).apply()

    companion object {
        const val DEVELOPER_NAME = "by ABDALKAYOUM MOUSAID."
        const val APP_TITLE = "surveillance-maladie"
        const val APP_VERSION = "1.0.12"
        const val CURRENT_VERSION_CODE = 16
        const val DEFAULT_UPDATE_URL = "https://raw.githubusercontent.com/abdocursor72/SF-Surveillance/main/version.json"

        private const val KEY_OBSERVER_NAME = "pref_observer_name"
        private const val KEY_EXPORT_LANG = "pref_export_lang"
        private const val KEY_EXPORT_QUALITY = "pref_export_quality"
        private const val KEY_INCLUDE_VIRUSES = "pref_include_viruses"
        private const val KEY_INCLUDE_PESTS = "pref_include_pests"
        private const val KEY_INCLUDE_BENEFICIALS = "pref_include_beneficials"
        private const val KEY_INCLUDE_NOTES = "pref_include_notes"
        private const val KEY_INCLUDE_PHOTO = "pref_include_photo"
        private const val KEY_THEME_MODE = "pref_theme_mode"
        private const val KEY_CUSTOM_SAMPLES = "pref_custom_samples"
        private const val KEY_AUTO_REFRESH = "pref_auto_refresh"
        private const val KEY_AUTO_REFRESH_INTERVAL = "pref_auto_refresh_interval"
        private const val KEY_UPDATE_SERVER_URL = "pref_update_server_url"
        private const val KEY_WHATSAPP_NUMBER = "pref_whatsapp_number"
        private const val KEY_WHATSAPP_GROUP_URL = "pref_whatsapp_group_url"
        private const val KEY_NOTIFICATIONS_ENABLED = "pref_notifications_enabled"
        private const val KEY_NOTIFY_APP_UPDATE = "pref_notify_app_update"
        private const val KEY_NOTIFY_BACKUP_REMINDER = "pref_notify_backup_reminder"
        private const val KEY_NOTIFY_DISEASE_THRESHOLD = "pref_notify_disease_threshold"
    }
}
