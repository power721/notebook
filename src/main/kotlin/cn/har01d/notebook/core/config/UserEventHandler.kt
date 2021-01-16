package cn.har01d.notebook.core.config

import cn.har01d.notebook.entity.UserRepository
import cn.har01d.notebook.service.AuditService
import cn.spark2fire.auth.event.UserEvent
import cn.spark2fire.auth.event.UserLoginEvent
import cn.spark2fire.auth.event.UserLogoutEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class UserEventHandler(private val auditService: AuditService,
                       private val userRepository: UserRepository) : ApplicationListener<UserEvent> {
    override fun onApplicationEvent(event: UserEvent) {
        val username: String = event.source as String
        val user = userRepository.findByUsername(username)
        if (user != null) {
            if (event is UserLoginEvent) {
                auditService.auditLogin(user)
            } else if (event is UserLogoutEvent) {
                auditService.auditLogout(user)
            }
        }
    }
}
