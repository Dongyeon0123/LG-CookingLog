package com.example.demo.controller

import com.example.demo.dto.CommentCreateRequest
import com.example.demo.dto.CommentResponse
import com.example.demo.dto.CommentUpdateRequest
import com.example.demo.service.CommentService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@CrossOrigin(
    origins = ["http://localhost:3000", "https://after-ungratifying-lilyanna.ngrok-free.dev"],
    allowCredentials = "true"
)
class CommentController(
    private val commentService: CommentService
) {
    
    @PostMapping
    fun createComment(
        @PathVariable postId: Long,
        @RequestHeader("User-Nickname") userNickname: String,
        @RequestBody request: CommentCreateRequest
    ): ResponseEntity<Any> {
        return try {
            // 수동 검증
            if (request.content.isBlank()) {
                return ResponseEntity.badRequest().body(mapOf("error" to "댓글 내용은 필수입니다"))
            }
            
            // 대댓글이 아닌 경우에만 별점 검증
            if (request.parentCommentId == null) {
                if (request.rating == null) {
                    return ResponseEntity.badRequest().body(mapOf("error" to "별점은 필수입니다"))
                }
                if (request.rating < 1 || request.rating > 5) {
                    return ResponseEntity.badRequest().body(mapOf("error" to "별점은 1-5 사이여야 합니다"))
                }
            }
            
            val response = commentService.createComment(postId, userNickname, request)
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: RuntimeException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
    
    @GetMapping
    fun getComments(
        @PathVariable postId: Long,
        @RequestHeader("User-Nickname", required = false) userNickname: String?
    ): ResponseEntity<List<CommentResponse>> {
        val comments = commentService.getCommentsByPostId(postId, userNickname)
        return ResponseEntity.ok(comments)
    }
    
    @GetMapping("/count")
    fun getCommentCount(@PathVariable postId: Long): ResponseEntity<Map<String, Long>> {
        val count = commentService.getCommentCount(postId)
        return ResponseEntity.ok(mapOf("count" to count))
    }
    
    @PutMapping("/{commentId}")
    fun updateComment(
        @PathVariable postId: Long,
        @PathVariable commentId: Long,
        @RequestHeader("User-Nickname") userNickname: String,
        @RequestBody request: CommentUpdateRequest
    ): ResponseEntity<Any> {
        return try {
            // 수동 검증
            if (request.content.isBlank()) {
                return ResponseEntity.badRequest().body(mapOf("error" to "댓글 내용은 필수입니다"))
            }
            
            val response = commentService.updateComment(commentId, userNickname, request)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
    
    @DeleteMapping("/{commentId}")
    fun deleteComment(
        @PathVariable postId: Long,
        @PathVariable commentId: Long,
        @RequestHeader("User-Nickname") userNickname: String
    ): ResponseEntity<Any> {
        return try {
            commentService.deleteComment(commentId, userNickname)
            ResponseEntity.ok().build()
        } catch (e: RuntimeException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
    
    @PostMapping("/{commentId}/like")
    fun toggleCommentLike(
        @PathVariable postId: Long,
        @PathVariable commentId: Long,
        @RequestHeader("User-Nickname") userNickname: String
    ): ResponseEntity<Any> {
        return try {
            val response = commentService.toggleCommentLike(commentId, userNickname)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
}