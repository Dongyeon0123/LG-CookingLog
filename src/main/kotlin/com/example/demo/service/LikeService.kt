package com.example.demo.service

import com.example.demo.entity.Like
import com.example.demo.entity.Post
import com.example.demo.entity.User
import com.example.demo.repository.LikeRepository
import com.example.demo.repository.PostRepository
import com.example.demo.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LikeService(
    private val likeRepository: LikeRepository,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) {
    
    /**
     * 좋아요 토글 (좋아요가 있으면 취소, 없으면 추가)
     */
    fun toggleLike(postId: Long, userNickname: String): Map<String, Any> {
        val post = postRepository.findById(postId)
            .orElseThrow { IllegalArgumentException("게시글을 찾을 수 없습니다.") }
        
        val user = userRepository.findByNickname(userNickname)
            ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다.")
        
        val existingLike = likeRepository.findByUserAndPost(user, post)
        
        return if (existingLike != null) {
            // 좋아요 취소
            likeRepository.delete(existingLike)
            mapOf(
                "liked" to false,
                "likeCount" to likeRepository.countByPost(post),
                "message" to "좋아요를 취소했습니다."
            )
        } else {
            // 좋아요 추가
            val like = Like(user = user, post = post)
            likeRepository.save(like)
            mapOf(
                "liked" to true,
                "likeCount" to likeRepository.countByPost(post),
                "message" to "좋아요를 눌렀습니다."
            )
        }
    }
    
    /**
     * 특정 게시글의 좋아요 상태 조회
     */
    @Transactional(readOnly = true)
    fun getLikeStatus(postId: Long, userNickname: String?): Map<String, Any> {
        val post = postRepository.findById(postId)
            .orElseThrow { IllegalArgumentException("게시글을 찾을 수 없습니다.") }
        
        val likeCount = likeRepository.countByPost(post)
        
        if (userNickname == null) {
            return mapOf(
                "liked" to false,
                "likeCount" to likeCount
            )
        }
        
        val user = userRepository.findByNickname(userNickname)
        val isLiked = if (user != null) {
            likeRepository.existsByUserAndPost(user, post)
        } else {
            false
        }
        
        return mapOf(
            "liked" to isLiked,
            "likeCount" to likeCount
        )
    }
    
    /**
     * 사용자가 좋아요한 게시글 목록 조회
     */
    @Transactional(readOnly = true)
    fun getLikedPostsByUser(userNickname: String): List<Post> {
        val user = userRepository.findByNickname(userNickname)
            ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다.")
        
        return likeRepository.findPostsLikedByUser(user)
    }
    
    /**
     * 특정 게시글의 좋아요 개수 조회
     */
    @Transactional(readOnly = true)
    fun getLikeCount(postId: Long): Long {
        val post = postRepository.findById(postId)
            .orElseThrow { IllegalArgumentException("게시글을 찾을 수 없습니다.") }
        
        return likeRepository.countByPost(post)
    }
}