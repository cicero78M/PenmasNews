package com.example.penmasnews.model

import android.content.Context
import android.content.SharedPreferences

/** Utility for storing CMS login details */
object CMSPrefs {
    private const val PREFS_NAME = "cms_login"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveBloggerToken(context: Context, token: String) {
        prefs(context).edit().putString("blogger_token", token).apply()
    }

    fun getBloggerToken(context: Context): String? =
        prefs(context).getString("blogger_token", null)

    fun saveWordpressCredentials(context: Context, baseUrl: String, user: String, appPass: String) {
        prefs(context).edit()
            .putString("wp_base_url", baseUrl)
            .putString("wp_user", user)
            .putString("wp_app_pass", appPass)
            .apply()
    }

    fun getWordpressBaseUrl(context: Context): String? =
        prefs(context).getString("wp_base_url", null)

    fun getWordpressUser(context: Context): String? =
        prefs(context).getString("wp_user", null)

    fun getWordpressAppPass(context: Context): String? =
        prefs(context).getString("wp_app_pass", null)
}
