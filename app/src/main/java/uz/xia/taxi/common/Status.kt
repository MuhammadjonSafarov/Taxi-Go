package uz.xia.taxi.common

import androidx.annotation.StringRes
import uz.xia.taxi.R

/**
 * 1) Информационные 100 - 199
 * 2) Успешные 200 - 299
 * 3) Перенаправления 300 - 399
 * 4) Клиентские ошибки 400 - 499
 * 5) Серверные ошибки 500 - 599
 */
sealed class Status {
    object Loading : Status()
    object Success : Status()
    class Error(@StringRes val errorMsg: Int = R.string.no_internet_connection) : Status()
}
/**
   * 1) 100-Continue
   * 2) 101-Switching Protocols
   * 3) 103-Early Hints
   * 4) 200-OK
   * 5) 201-Created
   * 6) 202-Accepted
   * 7) 203-Non-Authoritative Information
   * 8) 204-No Content
   * 9) 205-Reset Content
   * 10) 206-Partial Content
   * 11) 300-Multiple Choices
   * 12) 301-Moved Permanently
   * 13) 302-Found
   * 14) 303-See Other
   * 15) 304-Not Modified
   * 16) 307-Temporary Redirect
   * 17) 308-Permanent Redirect
   * 18) 400-Bad Request
   * 19) 401-Unauthorized
   * 20) 402-Payment Required
   * 21) 403-Forbidden
   * 22) 404-Not Found
   * 23) 405-Method Not Allowed
   * 24) 406-Not Acceptable
   * 25) 407-Proxy Authentication Required
   * 26) 408-Request Timeout
   * 27) 409-Conflict
   * 28) 410-Gone
   * 29) 411-Length Required
   * 30) 412-Precondition Failed
   * 31) 413-Payload Too Large
   * 32) 414-URI Too Long
   * 33) 415-Unsupported Media Type
   * 34) 416-Range Not Satisfiable
   * 35) 417-Expectation Failed
   * 36) 418-I'm a teapot
   * 37) 422-Unprocessable Entity
   * 38) 425-Too Early
   * 39  426-Upgrade Required
   * 40) 428-Precondition Required
   * 41) 429-Too Many Requests
   * 42) 431-Request Header Fields Too Large
   * 43) 451-Unavailable For Legal Reasons
   * 44) 500-Internal Server Error
   * 45) 501-Not Implemented
   * 46) 502-Bad Gateway
   * 47) 503-Service Unavailable
   * 48) 504-Gateway Timeout
   * 49) 505-HTTP Version Not Supported
   * 50) 506-Variant Also Negotiates
   * 51) 507-Insufficient Storage
   * 52) 508-Loop Detected
   * 53) 510-Not Extended
   * 54) 511-Network Authentication Required
 */

enum class ServerStatus {
    UNKNOWN,
    FORBIDDEN,
    SUCCESS
}
