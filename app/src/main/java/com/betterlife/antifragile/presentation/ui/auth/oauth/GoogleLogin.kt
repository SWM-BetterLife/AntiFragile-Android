package com.betterlife.antifragile.presentation.ui.auth.oauth

import android.content.Context
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GoogleLogin(private val activity: Context) {

    suspend fun startGoogleLogin(hashedNonce: String): String? {
        val credentialManager = CredentialManager.create(activity)

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("396393982817-tkt4h65qebasusc9jc9gc2c9kj16jgvf.apps.googleusercontent.com")
            .setAutoSelectEnabled(true)
            .setNonce(hashedNonce)
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(
                request = request,
                context = activity
            )
            handleSignIn(result, hashedNonce)
        } catch (e: GetCredentialException) {
            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
            null
        } catch (e: GoogleIdTokenParsingException) {
            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun handleSignIn(result: GetCredentialResponse, hashedNonce: String): String? {
        return when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        val googleIdToken = googleIdTokenCredential.idToken
                        val googleId = googleIdTokenCredential.id

                        val decodedToken = decodeJwt(googleIdToken)
                        val nonceFromToken = decodedToken?.get("nonce") as? String

                        if (nonceFromToken == hashedNonce) {
                            Log.d("TAG", "Nonce is valid. User ID: $googleId")
                            googleId
                        } else {
                            Log.e("TAG", "Invalid nonce. Expected: $hashedNonce, but received: $nonceFromToken")
                            null
                        }
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("TAG", "Received an invalid google id token response", e)
                        null
                    }
                } else {
                    Log.e("TAG", "Unexpected type of credential")
                    null
                }
            }
            else -> {
                Log.e("TAG", "Unexpected type of credential")
                null
            }
        }
    }

    private fun decodeJwt(token: String): Map<*, *>? {
        val parts = token.split(".")
        if (parts.size != 3) throw IllegalArgumentException("Invalid JWT token")

        val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP))
        return Gson().fromJson(payload, Map::class.java)
    }
}
