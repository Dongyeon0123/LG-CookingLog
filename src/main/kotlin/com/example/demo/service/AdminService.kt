package com.example.demo.service

import com.example.demo.dto.AdminUserResponse
import com.example.demo.repository.UserRepository
import com.example.demo.repository.PostRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class AdminService(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) {
    
    /**
     * 관리자용 사용자 목록 조회 (페이징, 검색 지원)
     */
    @Transactional(readOnly = true)
    fun getAllUsersForAdmin(pageable: Pageable, search: String?): Page<AdminUserResponse> {
        val users = if (search.isNullOrBlank()) {
            userRepository.findAll(pageable)
        } else {
            userRepository.findByUserIdContainingOrNicknameContainingOrPhoneNumberContaining(
                search, search, search, pageable
            )
        }
        
        return users.map { user ->
            val postCount = postRepository.countByUserNickname(user.nickname)
            
            AdminUserResponse(
                id = user.id,
                userId = user.userId,
                nickname = user.nickname,
                phoneNumber = user.phoneNumber,
                role = user.role,
                bio = user.bio,
                survey = user.survey,
                profileImageUrl = user.profileImageUrl,
                postCount = postCount,
                createdAt = LocalDateTime.now() // User 엔티티에 createdAt이 없으므로 임시로 현재 시간 사용
            )
        }
    }
    
    /**
     * 관리자용 사용자 상세 조회
     */
    @Transactional(readOnly = true)
    fun getUserDetailForAdmin(id: Long): AdminUserResponse? {
        return userRepository.findById(id).orElse(null)?.let { user ->
            val postCount = postRepository.countByUserNickname(user.nickname)
            
            AdminUserResponse(
                id = user.id,
                userId = user.userId,
                nickname = user.nickname,
                phoneNumber = user.phoneNumber,
                role = user.role,
                bio = user.bio,
                survey = user.survey,
                profileImageUrl = user.profileImageUrl,
                postCount = postCount,
                createdAt = LocalDateTime.now() // User 엔티티에 createdAt이 없으므로 임시로 현재 시간 사용
            )
        }
    }
    
    /**
     * 관리자용 사용자 삭제
     */
    fun deleteUserByAdmin(id: Long) {
        val user = userRepository.findById(id).orElse(null)
            ?: throw RuntimeException("존재하지 않는 사용자입니다.")
        
        // 사용자의 게시글도 함께 삭제할지 결정 (현재는 사용자만 삭제)
        userRepository.deleteById(id)
    }
    
    /**
     * 관리자용 사용자 통계
     */
    @Transactional(readOnly = true)
    fun getUserStats(): Map<String, Any> {
        val totalUsers = userRepository.count()
        val totalPosts = postRepository.count()
        
        // 최근 가입한 사용자들 (최근 10명)
        val recentUsers = userRepository.findAll(
            org.springframework.data.domain.PageRequest.of(0, 10, 
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id")
            )
        ).content.map { user ->
            mapOf(
                "id" to user.id,
                "userId" to user.userId,
                "nickname" to user.nickname,
                "role" to user.role
            )
        }
        
        return mapOf(
            "totalUsers" to totalUsers,
            "totalPosts" to totalPosts,
            "recentUsers" to recentUsers
        )
    }
}