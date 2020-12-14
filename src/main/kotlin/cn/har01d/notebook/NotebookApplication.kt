package cn.har01d.notebook

import cn.har01d.notebook.entity.Category
import cn.har01d.notebook.entity.CategoryRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NotebookApplication(private val categoryRepository: CategoryRepository) : CommandLineRunner {
    override fun run(vararg args: String?) {
        if (categoryRepository.count() == 0L) {
            categoryRepository.save(Category("默认分类", ""))
        }
    }
}

fun main(args: Array<String>) {
    runApplication<NotebookApplication>(*args)
}
