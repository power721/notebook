package cn.har01d.notebook.entity

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*

enum class ConfigType {
    BOOLEAN,
    INT,
    LONG,
    STRING,
    JSON
}

@Entity
class Config(
        @Id @Column(length = 32, nullable = false) val name: String,
        @Column(columnDefinition = "TEXT", nullable = false) var value: String,
        @Enumerated(EnumType.STRING) @Column(length = 10, nullable = false) var type: ConfigType = ConfigType.STRING
)

interface ConfigRepository : JpaRepository<Config, String>
