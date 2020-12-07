package cn.har01d.notebook.service

object IdUtils {
    const val NOTEBOOK_OFFSET = 10000
    const val USER_OFFSET = 20000
    const val CATEGORY_OFFSET = 30000
    private const val TOKEN = "p03uj8d6b2gzhln4eqkvxyairs5wft1o97cm"
    private const val TOKEN36 = "SNWZ9BL81RPU3XY7AOQCVHG4T5DJ2I0EK6FM"
    private const val TOKEN62 = "Ok4jShBpcvKY5gTQMVRsEHfGe3nDdb81IJwrqLFP0UC6xilazo2ZWut9yNmA7X"

    fun generate(len: Int = 6): String {
        val chars = TOKEN62.toCharArray()
        val sb = StringBuilder()
        for (i in 1..len) {
            sb.append(chars.random())
        }
        return sb.toString()
    }

    fun encode(number: Int): String {
        return encode(TOKEN, number)
    }

    fun encodeHex62(number: Int): String {
        return encode(TOKEN62, number)
    }

    fun encodeHex36(number: Int): String {
        return encode(TOKEN36, number)
    }

    private fun encode(token: String, number: Int): String {
        var number = number
        val chars = token.toCharArray()
        if (number == 0) {
            return String(chars, 0, 1)
        }
        val n = chars.size
        val sb = StringBuilder()
        while (number > 0) {
            sb.append(chars[(number % n).toInt()])
            number /= n.toInt()
        }
        return sb.reverse().toString()
    }

    fun decode(str: String): Int {
        return decode(TOKEN, str)
    }

    fun decodeHex62(str: String): Int {
        return decode(TOKEN62, str)
    }

    fun decodeHex36(str: String): Int {
        return decode(TOKEN36, str)
    }

    private fun decode(token: String, str: String): Int {
        var n: Int = 0
        val chars = token.toCharArray()
        for (c in str.toCharArray()) {
            var k = 0
            while (k < chars.size) {
                if (chars[k] == c) {
                    break
                }
                k++
            }
            n *= chars.size.toInt()
            n += k.toInt()
        }
        return n
    }
}
