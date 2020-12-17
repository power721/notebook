package cn.har01d.notebook.vo

data class SystemStats(
        val user: UserStats,
        val note: NoteStats,
        val notebooks: Long,
        val categories: Long,
        val tags: Long,
)

data class NoteStats(
        val total: Long,
        val deleted: Long,
        val views: Long,
        val public: Long,
        val secret: Long,
        val private: Long,
)

data class UserStats(
        val total: Long,
        val online: Long,
)
