package cn.har01d.notebook

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.env.Environment
import org.springframework.scheduling.annotation.EnableScheduling
import java.net.InetAddress
import java.net.UnknownHostException

@EnableScheduling
@SpringBootApplication
class NotebookApplication

private val log = LoggerFactory.getLogger(NotebookApplication::class.java)

fun main(args: Array<String>) {
    val context = runApplication<NotebookApplication>(*args)
    logApplicationStartup(context.environment)
}

private fun logApplicationStartup(env: Environment) {
    var protocol = "http"
    if (env.getProperty("server.ssl.key-store") != null) {
        protocol = "https"
    }
    val serverPort = env.getProperty("server.port", "8080")
    val contextPath = env.getProperty("server.servlet.context-path", "/")
    var hostAddress: String? = "localhost"
    try {
        hostAddress = InetAddress.getLocalHost().hostAddress
    } catch (e: UnknownHostException) {
        log.warn("The host name could not be determined, using `localhost` as fallback")
    }
    log.info(
        """
----------------------------------------------------------
	Application '{}' is running! Access URLs:
	Local: 		{}://localhost:{}{}
	External: 	{}://{}:{}{}
	Profile(s): 	{}
----------------------------------------------------------""",
        env.getProperty("spring.application.name", "Notebook"),
        protocol,
        serverPort,
        contextPath,
        protocol,
        hostAddress,
        serverPort,
        contextPath,
        env.activeProfiles
    )
}
