package cn.har01d.notebook.service

import cn.har01d.notebook.core.Access
import cn.har01d.notebook.core.ActionType
import cn.har01d.notebook.core.Const
import cn.har01d.notebook.core.SiteConfig
import cn.har01d.notebook.entity.*
import cn.har01d.notebook.vo.toVO
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Service
class AuditService(private val auditRepository: AuditRepository, private val configService: ConfigService) {

    fun list(pageable: Pageable) = auditRepository.findAll(pageable).map { it.toVO() }

    fun disabled() = !configService.get(Const.ENABLE_AUDIT, true)

    fun auditSiteConfig(user: User, old: SiteConfig, new: SiteConfig) {
        if (old.enableAudit != new.enableAudit) {
            val op = if (new.enableAudit) "开启审计" else "关闭审计"
            val audit = Audit(user, ActionType.CONFIG, op, 0, getUserAgent(), getClientIp(), getReferer())
            auditRepository.save(audit)
        } else if (!old.enableAudit) {
            return
        }
        if (old.enableComment != new.enableComment) {
            val op = if (new.enableComment) "允许评论" else "禁止评论"
            val audit = Audit(user, ActionType.CONFIG, op, 0, getUserAgent(), getClientIp(), getReferer())
            auditRepository.save(audit)
        }
        if (old.enableSignup != new.enableSignup) {
            val op = if (new.enableSignup) "允许注册" else "禁止注册"
            val audit = Audit(user, ActionType.CONFIG, op, 0, getUserAgent(), getClientIp(), getReferer())
            auditRepository.save(audit)
        }
        if (old.siteName != new.siteName) {
            val audit = Audit(user, ActionType.CONFIG, "更新站点名称：${new.siteName}", 0, getUserAgent(), getClientIp(), getReferer())
            auditRepository.save(audit)
        }
        if (old.brandColor != new.brandColor) {
            val audit = Audit(user, ActionType.CONFIG, "更新品牌颜色：${new.brandColor}", 0, getUserAgent(), getClientIp(), getReferer())
            auditRepository.save(audit)
        }
        if (old.qrCode != new.qrCode) {
            val audit = Audit(user, ActionType.CONFIG, "更新二维码", 0, getUserAgent(), getClientIp(), getReferer())
            auditRepository.save(audit)
        }
        if (old.icpBeian != new.icpBeian) {
            val audit = Audit(user, ActionType.CONFIG, "更新ICP备案：${new.icpBeian}", 0, getUserAgent(), getClientIp(), getReferer())
            auditRepository.save(audit)
        }
        if (old.govBeian != new.govBeian) {
            val audit = Audit(user, ActionType.CONFIG, "更新公安备案：${new.govBeian}", 0, getUserAgent(), getClientIp(), getReferer())
            auditRepository.save(audit)
        }
    }

    fun auditSignUp(user: User) {
        if (disabled()) {
            return
        }
        val audit = Audit(user, ActionType.SIGN_UP, "注册", user.id!!, getUserAgent(), getClientIp(), getReferer())
        auditRepository.save(audit)
    }

    fun auditUserUpdate(user: User) {
        if (disabled()) {
            return
        }
        val audit = Audit(user, ActionType.UPDATE_USER, "更新用户信息", user.id!!, getUserAgent(), getClientIp(), getReferer())
        auditRepository.save(audit)
    }

    fun auditLogin(user: User) {
        if (disabled()) {
            return
        }
        val audit = Audit(user, ActionType.LOGIN, "登录", user.id!!, getUserAgent(), getClientIp(), getReferer())
        auditRepository.save(audit)
    }

    fun auditLogout(user: User) {
        if (disabled()) {
            return
        }
        val audit = Audit(user, ActionType.LOGOUT, "注销", user.id!!, getUserAgent(), getClientIp(), getReferer())
        auditRepository.save(audit)
    }

    fun auditCategoryCreate(user: User, category: Category) {
        if (disabled()) {
            return
        }
        val audit = Audit(user, ActionType.CREATE_CATEGORY, "创建分类：${category.name}", category.id!!, getUserAgent(), getClientIp(), getReferer())
        auditRepository.save(audit)
    }

    fun auditCategoryUpdate(user: User, category: Category) {
        if (disabled()) {
            return
        }
        val audit = Audit(user, ActionType.UPDATE_CATEGORY, "更新分类：${category.name}", category.id!!, getUserAgent(), getClientIp(), getReferer())
        auditRepository.save(audit)
    }

    fun auditCategoryDelete(user: User, category: Category) {
        if (disabled()) {
            return
        }
        val audit = Audit(user, ActionType.DELETE_CATEGORY, "删除分类：${category.name}", category.id!!, getUserAgent(), getClientIp(), getReferer())
        auditRepository.save(audit)
    }

    fun auditNoteCreate(user: User, note: Note) {
        if (disabled()) {
            return
        }
        val op = if (note.access == Access.PRIVATE) "创建私有笔记：${note.content?.title}" else if (note.access == Access.SECRET) "创建秘密笔记：${note.content?.title}" else "创建公开笔记：${note.content?.title}"
        val audit = Audit(user, ActionType.CREATE_NOTE, op, note.id!!, getUserAgent(), getClientIp(), getReferer())
        auditRepository.save(audit)
    }

    fun auditNoteUpdate(user: User, note: Note, op: String = "更新笔记：${note.content?.title}") {
        if (disabled()) {
            return
        }
        val audit = Audit(user, ActionType.UPDATE_NOTE, op, note.id!!, getUserAgent(), getClientIp(), getReferer())
        auditRepository.save(audit)
    }

    fun auditNoteDelete(user: User, note: Note) {
        if (disabled()) {
            return
        }
        val audit = Audit(user, ActionType.DELETE_NOTE, "删除笔记：${note.content?.title}", note.id!!, getUserAgent(), getClientIp(), getReferer())
        auditRepository.save(audit)
    }

    fun auditNoteContentDelete(user: User, note: Note, version: Int) {
        if (disabled()) {
            return
        }
        val audit = Audit(user, ActionType.DELETE_NOTE, "删除笔记：${note.content?.title}版本$version", note.id!!, getUserAgent(), getClientIp(), getReferer())
        auditRepository.save(audit)
    }

    fun auditNoteDelete2(user: User, note: Note) {
        if (disabled()) {
            return
        }
        val audit = Audit(user, ActionType.DELETE_NOTE, "删除笔记：${note.content?.title}到回收站", note.id!!, getUserAgent(), getClientIp(), getReferer())
        auditRepository.save(audit)
    }

    fun auditCleanTrash(user: User) {
        if (disabled()) {
            return
        }
        val audit = Audit(user, ActionType.DELETE_NOTE, "清空回收站", user.id!!, getUserAgent(), getClientIp(), getReferer())
        auditRepository.save(audit)
    }

    fun auditNotebookCreate(user: User, notebook: Notebook) {
        if (disabled()) {
            return
        }
        val op = if (notebook.access == Access.PRIVATE) "创建私有笔记本：${notebook.name}" else if (notebook.access == Access.SECRET) "创建秘密笔记本：${notebook.name}" else "创建公开笔记本：${notebook.name}"
        val audit = Audit(user, ActionType.CREATE_NOTEBOOK, op, notebook.id!!, getUserAgent(), getClientIp(), getReferer())
        auditRepository.save(audit)
    }

    fun auditNotebookUpdate(user: User, notebook: Notebook) {
        if (disabled()) {
            return
        }
        val audit = Audit(user, ActionType.UPDATE_NOTEBOOK, "更新笔记本：${notebook.name}", notebook.id!!, getUserAgent(), getClientIp(), getReferer())
        auditRepository.save(audit)
    }

    fun auditNotebookDelete(user: User, notebook: Notebook) {
        if (disabled()) {
            return
        }
        val audit = Audit(user, ActionType.DELETE_NOTEBOOK, "删除笔记本：${notebook.name}", notebook.id!!, getUserAgent(), getClientIp(), getReferer())
        auditRepository.save(audit)
    }

    fun auditTagCreate(user: User, tag: Tag) {
        if (disabled()) {
            return
        }
        val audit = Audit(user, ActionType.CREATE_TAG, "创建标签：${tag.name}", tag.id!!, getUserAgent(), getClientIp(), getReferer())
        auditRepository.save(audit)
    }

    fun auditTagUpdate(user: User, tag: Tag) {
        if (disabled()) {
            return
        }
        val audit = Audit(user, ActionType.UPDATE_TAG, "更新标签：${tag.name}", tag.id!!, getUserAgent(), getClientIp(), getReferer())
        auditRepository.save(audit)
    }

    fun auditTagDelete(user: User, tag: Tag) {
        if (disabled()) {
            return
        }
        val audit = Audit(user, ActionType.DELETE_TAG, "删除标签：${tag.name}", tag.id!!, getUserAgent(), getClientIp(), getReferer())
        auditRepository.save(audit)
    }

    private fun getClientIp(): String? {
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?)!!.request
        val ip = request.getHeader("X-Forwarded-For")
        return if (ip != null && ip.isNotEmpty()) {
            ip
        } else {
            request.remoteAddr
        }
    }

    private fun getUserAgent(): String? {
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?)!!.request
        return request.getHeader("User-Agent")
    }

    private fun getReferer(): String? {
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?)!!.request
        return request.getHeader("Referer")
    }
}
