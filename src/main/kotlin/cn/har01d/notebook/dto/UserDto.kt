package cn.har01d.notebook.dto

data class UserDto(val email: String, val password: String, val newPassword: String)

data class UserInfoDto(val editorMode: String, val mdTheme: String, val signature: String)
