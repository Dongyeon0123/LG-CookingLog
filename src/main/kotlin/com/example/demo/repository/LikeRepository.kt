package com.example.demo.repository

import com.example.demo.entity.Like
import com.example.demo.entity.Post
import com.example.demo.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface LikeRepository : JpaRepository<Like, Long> {
    
    // 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
    fun existsByUserAndPost(user: User, post: Post): Boolean
    
    // 특정 사용자가 특정 게시글에 누른 좋아요 찾기
    fun findByUserAndPost(user: User, post: Post): Like?
    
    // 특정 게시글의 좋아요 개수 조회
    fun countByPost(post: Post): Long
    
    // 특정 게시글의 모든 좋아요 조회
    fun findByPost(post: Post): List<Like>
    
    // 특정 사용자가 좋아요한 게시글들 조회
    @Query("SELECT l.post FROM Like l WHERE l.user = :user ORDER BY l.createdAt DESC")
    fun findPostsLikedByUser(@Param("user") user: User): List<Post>
}