package com.example.demo.controller

import com.example.demo.service.AdminService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(
    origins = ["http://localhost:3000", "https://after-ungratifying-lilyanna.ngrok-free.dev"],
    allowCredentials = "true",
    allowedHeaders = ["*"],
    methods = [RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS]
)
class AdminController(
    private val adminService: AdminService
) {
    
    /**
     * 관리자용 사용자 목록 조회 (페이징, 검색 지원)
     */
    @GetMapping("/users")
    fun getAllUsersForAdmin(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(defaultValue = "id") sortBy: String,
        @RequestParam(defaultValue = "desc") sortDir: String,
        @RequestParam(required = false) search: String?
    ): ResponseEntity<Map<String, Any>> {
        val direction = if (sortDir.lowercase() == "asc") Sort.Direction.ASC else Sort.Direction.DESC
        val pageable = PageRequest.of(page, size, Sort.by(direction, sortBy))
        
        val result = adminService.getAllUsersForAdmin(pageable, search)
        
        return ResponseEntity.ok(mapOf(
            "users" to result.content,
            "totalElements" to result.totalElements,
            "totalPages" to result.totalPages,
            "currentPage" to result.number,
            "size" to result.size,
            "hasNext" to result.hasNext(),
            "hasPrevious" to result.hasPrevious()
        ))
    }
    
    /**
     * 관리자용 사용자 상세 조회
     */
    @GetMapping("/users/{id}")
    fun getUserDetailForAdmin(@PathVariable id: Long): ResponseEntity<Any> {
        val user = adminService.getUserDetailForAdmin(id)
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    /**
     * 관리자용 사용자 삭제
     */
    @DeleteMapping("/users/{id}")
    fun deleteUserByAdmin(@PathVariable id: Long): ResponseEntity<Map<String, String>> {
        return try {
            adminService.deleteUserByAdmin(id)
            ResponseEntity.ok(mapOf("message" to "사용자가 성공적으로 삭제되었습니다."))
        } catch (e: RuntimeException) {
            ResponseEntity.badRequest().body(mapOf("error" to (e.message ?: "삭제 중 오류가 발생했습니다.")))
        }
    }
    
    /**
     * 관리자용 사용자 통계
     */
    @GetMapping("/users/stats")
    fun getUserStats(): ResponseEntity<Map<String, Any>> {
        val stats = adminService.getUserStats()
        return ResponseEntity.ok(stats)
    }
}