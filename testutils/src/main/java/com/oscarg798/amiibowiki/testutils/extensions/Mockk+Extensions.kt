package com.oscarg798.amiibowiki.testutils.extensions

import io.mockk.MockK
import io.mockk.MockKDsl
import io.mockk.MockKVerificationScope
import io.mockk.Ordering
import kotlin.reflect.KClass

inline fun <reified T : Any> relaxedMockk(
    name: String? = null,
    vararg moreInterfaces: KClass<*>,
    block: T.() -> Unit = {}
): T = MockK.useImpl {
    MockKDsl.internalMockk(
        name,
        true,
        *moreInterfaces,
        relaxUnitFun = true,
        block = block
    )
}

typealias VerifyBlock = suspend MockKVerificationScope.() -> Unit

fun coVerifyWasNotCalled(
    ordering: Ordering = Ordering.UNORDERED,
    inverse: Boolean = false,
    atLeast: Int = 1,
    atMost: Int = Int.MAX_VALUE,
    timeout: Long = 0,
    verifyBlock: VerifyBlock
) = MockK.useImpl {
    MockKDsl.internalCoVerify(
        ordering,
        inverse,
        atLeast,
        atMost,
        0,
        timeout,
        verifyBlock
    )
}
