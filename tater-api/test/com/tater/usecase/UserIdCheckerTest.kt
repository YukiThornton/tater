package com.tater.usecase

import com.tater.AutoResetMock
import com.tater.domain.UserId
import io.mockk.impl.annotations.InjectMockKs
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withMessage
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("UserIdChecker")
class UserIdCheckerTest: AutoResetMock {

    @InjectMockKs
    private lateinit var sut: UserIdChecker

    @Nested
    @DisplayName("makeSureUserIdExists")
    inner class MakeSureUserIdExists {

        @Test
        fun `Returns UserId when given argument is not null`() {
            sut.makeSureUserIdExists(UserId("id1")) shouldBeEqualTo UserId(("id1"))
        }

        @Test
        fun `Throws a UserNotSpecifiedException when given argument is null`() {
            { sut.makeSureUserIdExists(null) } shouldThrow UserNotSpecifiedException::class withMessage "UserId is missing"
        }
    }
}