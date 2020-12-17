package cn.har01d.notebook.service

import cn.har01d.notebook.vo.SystemInfo
import org.springframework.stereotype.Service
import java.net.InetAddress
import java.util.*


@Service
class AdminService {
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
}
