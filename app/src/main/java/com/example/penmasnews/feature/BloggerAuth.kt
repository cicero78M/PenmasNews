package com.example.penmasnews.feature

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.example.penmasnews.BuildConfig
import com.example.penmasnews.util.DebugLogger
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.AuthState
import net.openid.appauth.ResponseTypeValues

object BloggerAuth {
    const val RC_SIGN_IN = 9001

    private val AUTH_ENDPOINT = Uri.parse("https://accounts.google.com/o/oauth2/v2/auth")
    private val TOKEN_ENDPOINT = Uri.parse("https://oauth2.googleapis.com/token")
    private val REDIRECT_URI = Uri.parse("com.example.penmasnews:/oauth2redirect")

    private var service: AuthorizationService? = null
    private var authState: AuthState? = null

    fun startLogin(activity: Activity, callback: (String?) -> Unit) {
        DebugLogger.log(activity, "Starting OAuth login")
        val config = AuthorizationServiceConfiguration(AUTH_ENDPOINT, TOKEN_ENDPOINT)
        val request = AuthorizationRequest.Builder(
            config,
            BuildConfig.BLOGGER_CLIENT_ID,
            ResponseTypeValues.CODE,
            REDIRECT_URI
        ).setScopes("https://www.googleapis.com/auth/blogger", "email")
            .build()
        service = AuthorizationService(activity)
        val intent = service!!.getAuthorizationRequestIntent(request)
        activity.startActivityForResult(intent, RC_SIGN_IN)
    }

    fun handleAuthResponse(activity: Activity, data: Intent?, callback: (String?) -> Unit) {
        val resp = AuthorizationResponse.fromIntent(data!!)
        val ex = AuthorizationException.fromIntent(data)
        if (resp == null) {
            DebugLogger.log(activity, "Authorization failed: ${ex?.message}")
            callback(null)
            return
        }
        authState = AuthState(resp, ex)
        val tokenRequest = resp.createTokenExchangeRequest()
        service?.performTokenRequest(tokenRequest) { tokenResp, tokenEx ->
            if (tokenResp != null) {
                authState?.update(tokenResp, tokenEx)
                DebugLogger.log(activity, "Retrieved token length: ${tokenResp.accessToken?.length}")
                callback(tokenResp.accessToken)
            } else {
                DebugLogger.log(activity, "Token exchange failed: ${tokenEx?.message}")
                callback(null)
            }
        }
    }
}
