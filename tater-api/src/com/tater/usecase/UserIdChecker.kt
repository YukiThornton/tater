package com.tater.usecase

import com.tater.domain.UserId

class UserIdChecker {
    fun makeSureUserIdExists(userId: UserId?): UserId {
        if (userId == null) throw UserNotSpecifiedException("UserId is missing")
        return userId
    }
}

class UserNotSpecifiedException(override val message: String?) : RuntimeException()