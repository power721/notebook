package cn.har01d.notebook.util

import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDate
import java.util.*


fun copy(input: InputStream, output: OutputStream) {
    var count: Long = 0
    val buffer = ByteArray(4096)
    var n: Int
    while (-1 != input.read(buffer).also { n = it }) {
        output.write(buffer, 0, n)
        count += n.toLong()
    }
}

fun generateFileName(): String {
    val name = LocalDate.now().toString() + UUID.randomUUID().toString()
    return name.replace("-", "")
}

fun wordCount(context: String): Int {
    val cnWords = context.replace("[^(\\u4e00-\\u9fa5，。《》？；’‘：“”【】、）（……￥！·)]".toRegex(), "")
    val cnWordsCount = cnWords.length

    val nonCnWords = context.replace("[^(a-zA-Z0-9`\\-=\';.,/~!@#$%^&*()_+|}{\":><?\\[\\])]".toRegex(), " ")
    var nonCnWordsCount = 0
    val words = nonCnWords.split(" ").toTypedArray()
    for (word in words) {
        if (word.trim { it <= ' ' }.isNotEmpty()) nonCnWordsCount++
    }

    return cnWordsCount + nonCnWordsCount
}
