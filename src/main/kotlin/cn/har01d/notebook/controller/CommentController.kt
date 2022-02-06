package cn.har01d.notebook.controller

import cn.har01d.notebook.core.exception.AppException
import cn.har01d.notebook.core.exception.AppForbiddenException
import cn.har01d.notebook.core.exception.AppNotFoundException
import cn.har01d.notebook.entity.CommentRepository
import cn.har01d.notebook.service.UserService
import cn.har01d.notebook.vo.CommentVo2
import cn.har01d.notebook.vo.toVo2
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*


@RequestMapping("/comments")
@RestController
class CommentController(private val commentRepository: CommentRepository, private val userService: UserService) {
    @PostMapping("/{id}/stick")
    fun stick(@PathVariable id: Int): CommentVo2 {
        val comment = commentRepository.findByIdOrNull(id) ?: throw AppNotFoundException("评论不存在")
        val user = userService.requireCurrentUser()
        if (user.id != comment.user.id) {
            throw AppForbiddenException("无权操作")
        }

        val comments = commentRepository.findByNoteAndStickyTrue(comment.note, Sort.unsorted())
        if (comments.size >= 3) {
            throw AppException("置顶评论数量已经达到上限")
        }
        comment.sticky = true
        return commentRepository.save(comment).toVo2()
    }

    @DeleteMapping("/{id}/stick")
    fun unstick(@PathVariable id: Int) {
        val comment = commentRepository.findByIdOrNull(id) ?: throw AppNotFoundException("评论不存在")
        val user = userService.requireCurrentUser()
        if (user.id != comment.user.id) {
            throw AppForbiddenException("无权操作")
        }

        comment.sticky = false
        commentRepository.save(comment)
    }
}
