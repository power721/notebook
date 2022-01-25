package cn.har01d.notebook.core.exception

import cn.har01d.notebook.core.Error
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
open class AppException : RuntimeException {
    val code: Int
    constructor(message: String, code: Int = Error.GENERAL_ERROR.code) : super(message) {
        this.code = code
    }

    constructor(message: String, code: Int, cause: Throwable?) : super(message, cause) {
        this.code = code
    }
}

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class AppUnauthorizedException : AppException {
    constructor(message: String, code: Int = Error.GENERAL_ERROR.code) : super(message, code)
    constructor(message: String, error: Error) : super(message, error.code)
    constructor(message: String, cause: Throwable?) : super(message, 0, cause)
    constructor(message: String, code: Int, cause: Throwable?) : super(message, code, cause)
    constructor(message: String, error: Error, cause: Throwable?) : super(message, error.code, cause)
}

@ResponseStatus(HttpStatus.FORBIDDEN)
class AppForbiddenException : AppException {
    constructor(message: String, code: Int = Error.GENERAL_ERROR.code) : super(message, code)
    constructor(message: String, error: Error) : super(message, error.code)
    constructor(message: String, cause: Throwable?) : super(message, 0, cause)
    constructor(message: String, code: Int, cause: Throwable?) : super(message, code, cause)
    constructor(message: String, error: Error, cause: Throwable?) : super(message, error.code, cause)
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class AppNotFoundException : AppException {
    constructor(message: String, code: Int = Error.GENERAL_ERROR.code) : super(message, code)
    constructor(message: String, error: Error) : super(message, error.code)
    constructor(message: String, code: Int, cause: Throwable?) : super(message, code, cause)
    constructor(message: String, error: Error, cause: Throwable?) : super(message, error.code, cause)
}
