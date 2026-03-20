package com.qrmobil.admin.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class RestaurantInfoViewModel(application: Application) : AndroidViewModel(application) {
    var restaurantName by mutableStateOf("")
    var phone by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    init {
        loadData()
    }

    private fun loadData() {
        val sharedPref = getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        restaurantName = sharedPref.getString("restaurantName", "") ?: ""
        phone = sharedPref.getString("phone", "") ?: ""
        email = sharedPref.getString("email", "") ?: ""
        password = sharedPref.getString("password", "") ?: ""
    }

    fun saveInfo(onSuccess: () -> Unit) {
        val sharedPref = getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("restaurantName", restaurantName)
            putString("phone", phone)
            putString("email", email)
            if (password.isNotBlank()) {
                putString("password", password)
            }
            apply()
        }
        onSuccess()
    }
}
