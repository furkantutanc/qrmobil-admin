package com.qrmobil.admin.network

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("api/register")
    suspend fun register(
        @Field("_token") token: String,
        @Field("restoranAdi") restoranAdi: String,
        @Field("restoranSlogani") restoranSlogani: String,
        @Field("restoranAdresi") restoranAdresi: String,
        @Field("restoranSehir") restoranSehir: String,
        @Field("restoranIlce") restoranIlce: String,
        @Field("restoranGunPztStart") restoranGunPztStart: String,
        @Field("restoranGunPztEnd") restoranGunPztEnd: String,
        @Field("restoranGunSalStart") restoranGunSalStart: String,
        @Field("restoranGunSalEnd") restoranGunSalEnd: String,
        @Field("restoranGunCarStart") restoranGunCarStart: String,
        @Field("restoranGunCarEnd") restoranGunCarEnd: String,
        @Field("restoranGunPerStart") restoranGunPerStart: String,
        @Field("restoranGunPerEnd") restoranGunPerEnd: String,
        @Field("restoranGunCumStart") restoranGunCumStart: String,
        @Field("restoranGunCumEnd") restoranGunCumEnd: String,
        @Field("restoranGunCtesiStart") restoranGunCtesiStart: String,
        @Field("restoranGunCtesiEnd") restoranGunCtesiEnd: String,
        @Field("restoranGunPazStart") restoranGunPazStart: String,
        @Field("restoranGunPazEnd") restoranGunPazEnd: String,
        @Field("RestoranMail") restoranMail: String,
        @Field("RestoranTelefon") restoranTelefon: String,
        @Field("RestoranSifre") restoranSifre: String,
        @Field("RestoranSifreTekrar") restoranSifreTekrar: String
    ): Response<okhttp3.ResponseBody>

    @GET("api/categories")
    suspend fun getCategories(
        @Query("restoran_id") restoranId: Int
    ): Response<okhttp3.ResponseBody>

    @FormUrlEncoded
    @POST("api/categories")
    suspend fun addCategory(
        @Field("_token") token: String,
        @Field("restoran_id") restoranId: Int,
        @Field("kategoriAdi") kategoriAdi: String
    ): Response<okhttp3.ResponseBody>
}

