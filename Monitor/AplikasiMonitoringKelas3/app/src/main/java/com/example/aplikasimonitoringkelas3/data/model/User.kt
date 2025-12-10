package com.example.aplikasimonitoringkelas3.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int? = null,
    
    @SerializedName("nama")
    val nama: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String? = null,
    
    @SerializedName("role")
    val role: String,
    
    @SerializedName("created_at")
    val createdAt: String? = null,
    
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

data class LoginRequest(
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String
)

data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: LoginData?
)

data class LoginData(
    @SerializedName("user")
    val user: User,
    
    @SerializedName("token")
    val token: String
)

data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String? = null,
    
    @SerializedName("data")
    val data: T? = null,
    
    @SerializedName("errors")
    val errors: Map<String, List<String>>? = null
)
