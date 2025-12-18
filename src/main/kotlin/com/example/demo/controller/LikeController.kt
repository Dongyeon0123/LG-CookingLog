package com.example.demo.controller

import com.example.demo.service.LikeService
import com.example.demo.util.JwtUtil
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/likes")
@CrossOrigin(
    origins = ["http://localhost:3000", "https://after-ungratifying-lilyanna.ngrok-free.dev"],
    allowCredentials = "true"
)
class LikeController(
    private val likeService: LikeService,
    private val jwtUtil: JwtUtil
) {
    
    private val logger = LoggerFactory.getLogger(LikeController::class.java)
    
    /**
     * 좋아요 토글 (좋아요 추가/취소)
     */
    @PostMapping("/toggle/{postId}")
    fun toggleLike(
        @PathVariable postId: Long,
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<Map<String, Any>> {
        return try {
            logger.debug("좋아요 토글 요청: postId={}, authHeader={}", postId, authHeader)
            
            val token = authHeader.removePrefix("Bearer ")
            val userNickname = jwtUtil.extractNickname(token)
            
            logger.debug("추출된 사용자 닉네임: {}", userNickname)
            
            if (!jwtUtil.validateToken(token, userNickname)) {
                return ResponseEntity.status(401).body(
                    mapOf("error" to "유효하지 않은 토큰입니다." as Any)
                )
            }
            
            val result = likeService.toggleLike(postId, userNickname)
            logger.debug("좋아요 토글 결과: {}", result)
            ResponseEntity.ok(result)
            
        } catch (e: IllegalArgumentException) {
            logger.error("좋아요 토글 중 잘못된 인수 오류: ", e)
            ResponseEntity.badRequest().body(
                mapOf("error" to (e.message ?: "잘못된 요청입니다.") as Any)
            )
        } catch (e: Exception) {
            logger.error("좋아요 토글 중 서버 오류: ", e)
            ResponseEntity.status(500).body(
                mapOf("error" to "서버 오류가 발생했습니다." as Any)
            )
        }
    }
    
    /**
     * 특정 게시글의 좋아요 상태 조회
     */
    @GetMapping("/status/{postId}")
    fun getLikeStatus(
        @PathVariable postId: Long,
        @RequestHeader(value = "Authorization", required = false) authHeader: String?
    ): ResponseEntity<Map<String, Any>> {
        return try {
            val userNickname = if (authHeader != null) {
                val token = authHeader.removePrefix("Bearer ")
                if (jwtUtil.validateToken(token, jwtUtil.extractNickname(token))) {
                    jwtUtil.extractNickname(token)
                } else {
                    null
                }
            } else {
                null
            }
            
            val result = likeService.getLikeStatus(postId, userNickname)
            ResponseEntity.ok(result)
            
        } catch (e: IllegalArgumentException) {
            logger.error("좋아요 상태 조회 중 잘못된 인수 오류: ", e)
            ResponseEntity.badRequest().body(
                mapOf("error" to (e.message ?: "잘못된 요청입니다.") as Any)
            )
        } catch (e: Exception) {
            logger.error("좋아요 상태 조회 중 서버 오류: ", e)
            ResponseEntity.status(500).body(
                mapOf("error" to "서버 오류가 발생했습니다." as Any)
            )
        }
    }
    
    /**
     * 사용자가 좋아요한 게시글 목록 조회
     */
    @GetMapping("/my-likes")
    fun getMyLikedPosts(
        @RequestHeader("Authorization") authHeader: String
    ): ResponseEntity<Any> {
        return try {
            val token = authHeader.removePrefix("Bearer ")
            val userNickname = jwtUtil.extractNickname(token)
            
            if (!jwtUtil.validateToken(token, userNickname)) {
                return ResponseEntity.status(401).body(
                    mapOf("error" to "유효하지 않은 토큰입니다." as Any)
                )
            }
            
            val likedPosts = likeService.getLikedPostsByUser(userNickname)
            ResponseEntity.ok(likedPosts)
            
        } catch (e: IllegalArgumentException) {
            logger.error("좋아요한 게시글 목록 조회 중 잘못된 인수 오류: ", e)
            ResponseEntity.badRequest().body(
                mapOf("error" to (e.message ?: "잘못된 요청입니다.") as Any)
            )
        } catch (e: Exception) {
            logger.error("좋아요한 게시글 목록 조회 중 서버 오류: ", e)
            ResponseEntity.status(500).body(
                mapOf("error" to "서버 오류가 발생했습니다." as Any)
            )
        }
    }
    
    /**
     * 특정 게시글의 좋아요 개수 조회
     */
    @GetMapping("/count/{postId}")
    fun getLikeCount(@PathVariable postId: Long): ResponseEntity<Map<String, Any>> {
        return try {
            val count = likeService.getLikeCount(postId)
            ResponseEntity.ok(mapOf("likeCount" to count as Any))
            
        } catch (e: IllegalArgumentException) {
            logger.error("좋아요 개수 조회 중 잘못된 인수 오류: ", e)
            ResponseEntity.badRequest().body(
                mapOf("error" to (e.message ?: "잘못된 요청입니다.") as Any)
            )
        } catch (e: Exception) {
            logger.error("좋아요 개수 조회 중 서버 오류: ", e)
            ResponseEntity.status(500).body(
                mapOf("error" to "서버 오류가 발생했습니다." as Any)
            )
        }
    }
}