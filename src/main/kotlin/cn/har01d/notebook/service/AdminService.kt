package cn.har01d.notebook.service

import cn.har01d.notebook.core.Access
import cn.har01d.notebook.entity.CategoryRepository
import cn.har01d.notebook.entity.NoteRepository
import cn.har01d.notebook.entity.NotebookRepository
import cn.har01d.notebook.entity.UserRepository
import cn.har01d.notebook.vo.NoteStats
import cn.har01d.notebook.vo.SystemInfo
import cn.har01d.notebook.vo.SystemStats
import cn.har01d.notebook.vo.UserStats
import org.springframework.stereotype.Service
import java.net.InetAddress
import java.util.*


@Service
class AdminService(
        private val userRepository: UserRepository,
        private val noteRepository: NoteRepository,
        private val notebookRepository: NotebookRepository,
        private val categoryRepository: CategoryRepository
) {
    fun getSystemInfo(): SystemInfo {
        val runtime = Runtime.getRuntime()
        val props: Properties = System.getProperties()
        val addr: InetAddress = InetAddress.getLocalHost()
        return SystemInfo(
                addr.hostAddress,
                addr.hostName,
                runtime.totalMemory(),
                runtime.freeMemory(),
                runtime.availableProcessors(),
                props.getProperty("java.version"),
                props.getProperty("java.vendor"),
                props.getProperty("java.home"),
                props.getProperty("java.vm.name"),
                props.getProperty("os.name"),
                props.getProperty("os.version"),
                props.getProperty("os.arch"),
                props.getProperty("user.name"),
                props.getProperty("user.home"),
                props.getProperty("user.timezone"),
                props.getProperty("user.dir"),
                props.getProperty("PID"),
        )
    }

    fun getSystemStats(): SystemStats {
        return SystemStats(
                UserStats(
                        userRepository.count(),
                        0, // TODO:
                ),
                NoteStats(
                        noteRepository.countByDeleted(false),
                        noteRepository.countByDeleted(true),
                        noteRepository.views(),
                        noteRepository.countByAccess(Access.PUBLIC),
                        noteRepository.countByAccess(Access.SECRET),
                        noteRepository.countByAccess(Access.PRIVATE),
                ),
                notebookRepository.count(),
                categoryRepository.count(),
                0, // TODO:
        )
    }
}
