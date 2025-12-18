package com.example.demo.dto

import com.example.demo.entity.Role
import java.time.LocalDateTime

data class AdminUserResponse(
    val id: Long,
    val userId: String,
    val nickname: String,
    val phoneNumber: String,
    val role: Role,
    val bio: String?,
    val survey: String?,
    val profileImageUrl: String?,
    val postCount: Long,
    val createdAt: LocalDateTime
)