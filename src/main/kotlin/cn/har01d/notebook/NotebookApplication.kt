package cn.har01d.notebook

import cn.har01d.notebook.entity.Category
import cn.har01d.notebook.entity.CategoryRepository
import cn.har01d.notebook.entity.Menu
import cn.har01d.notebook.entity.MenuRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NotebookApplication(
        private val categoryRepository: CategoryRepository,
        private val menuRepository: MenuRepository,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        // TODO: init.sql, data.sql
        if (categoryRepository.count() == 0L) {
            categoryRepository.save(Category("默认分类", ""))
        }
        if (menuRepository.count() == 0L) {
            menuRepository.save(Menu("笔记", "/notes", "home", 1))
            menuRepository.save(Menu("笔记本", "/notebooks", "book", 2))
            menuRepository.save(Menu("分类", "/categories", "idea", 3))
            menuRepository.save(Menu("标签", "/tags", "tag", 4))
            menuRepository.save(Menu("关于", "/about", "info", 5))
        }
    }
}

fun main(args: Array<String>) {
    runApplication<NotebookApplication>(*args)
}
