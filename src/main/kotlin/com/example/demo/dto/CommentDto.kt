package com.example.demo.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class CommentCreateRequest(
    val content: String,
    val rating: Int? = null, // 대댓글인 경우 null
    val parentCommentId: Long? = null
)

data class CommentUpdateRequest(
    val content: String,
    val rating: Int? = null // 대댓글인 경우 null
)

data class CommentResponse(
    val id: Long,
    val content: String,
    val postId: Long,
    val userNickname: String,
    val userProfileImageUrl: String?,
    val rating: Int? = null, // 대댓글인 경우 null
    val likeCount: Int? = null, // 대댓글인 경우 null
    val isLikedByUser: Boolean? = null, // 대댓글인 경우 null
    val parentCommentId: Long?,
    val replies: List<CommentResponse> = emptyList(),
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)