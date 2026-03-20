package com.qrmobil.admin.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    var phoneOrEmail by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun login(context: Context, onLoginSuccess: () -> Unit) {
        val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val savedPhone = sharedPref.getString("phone", null)
        val savedEmail = sharedPref.getString("email", null)
        val savedPassword = sharedPref.getString("password", null)

        if ((phoneOrEmail == savedPhone || phoneOrEmail == savedEmail) && password == savedPassword) {
            errorMessage = null
            onLoginSuccess()
        } else {
            errorMessage = "Telefon numarası/E-posta veya şifre hatalı."
        }
    }

    fun clearError() {
        errorMessage = null
    }
}
