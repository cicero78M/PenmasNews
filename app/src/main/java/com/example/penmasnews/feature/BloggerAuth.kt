package com.example.penmasnews.feature

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.GoogleAuthUtil
import com.example.penmasnews.BuildConfig
import com.example.penmasnews.util.DebugLogger
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope

object BloggerAuth {
    const val RC_SIGN_IN = 9001
    private var client: GoogleSignInClient? = null

    fun getClient(activity: Activity): GoogleSignInClient {
        if (client == null) {
            val builder = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Scope("https://www.googleapis.com/auth/blogger"))

            val clientId = BuildConfig.BLOGGER_CLIENT_ID
            if (clientId.isNotBlank()) {
                builder.requestIdToken(clientId)
            }

            client = GoogleSignIn.getClient(activity, builder.build())
        }
        return client!!
    }

    fun getSignedInAccount(activity: Activity): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(activity)
    }

    fun signIn(activity: Activity) {
        DebugLogger.log(activity, "Launching Google sign in")
        val signInIntent: Intent = getClient(activity).signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun signOut(activity: Activity) {
        DebugLogger.log(activity, "Signing out of Google")
        getClient(activity).signOut()
    }

    fun getAuthToken(activity: Activity, account: GoogleSignInAccount): String? {
        val googleAccount = account.account ?: return null
        return try {
            val token = GoogleAuthUtil.getToken(
                activity,
                googleAccount,
                "oauth2:https://www.googleapis.com/auth/blogger"
            )
            DebugLogger.log(activity, "Retrieved token length: ${'$'}{token.length}")
            token
        } catch (e: Exception) {
            DebugLogger.log(activity, "Token retrieval failed: ${'$'}{e.message}")
            null
        }
    }
}

