package com.qrmobil.admin.network

import com.google.gson.annotations.SerializedName

data class RegistrationResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: RegistrationData?
)

data class RegistrationData(
    @SerializedName("restoran_id")
    val restoranId: Int
)
