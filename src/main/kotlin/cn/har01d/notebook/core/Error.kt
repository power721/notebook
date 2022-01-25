package cn.har01d.notebook.core

enum class Error(val code: Int) {
    GENERAL_ERROR(10000),
    CAPTCHA_THRESHOLD(10001),
    CAPTCHA_ERROR(10002),
    LOGIN_FAILED_MAX(10003),
    USER_NO_AUTH(10004),
    USER_NOT_EXIST(10005),
}
