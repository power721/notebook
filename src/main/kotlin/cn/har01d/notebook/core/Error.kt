package cn.har01d.notebook.core

enum class Error(val code: Int) {
    GENERAL_ERROR(10000),
    LOGIN_FAILED_MAX(10001),
    CAPTCHA_ERROR(10002),
    USER_NO_AUTH(10003),
    USER_NOT_EXIST(10004),
}
