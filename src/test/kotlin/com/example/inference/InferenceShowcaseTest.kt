package com.example.inference

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isFailure
import strikt.assertions.isSuccess

class InferenceShowcaseTest {

    @Nested
    inner class ReifiedToReified {
        @Test
        fun `should succeed when getting First as First`() {
            expectCatching {
                returnValue = First("foo")
                getFirstViaReifiedReifiedChain()
            }.isSuccess().isA<First>()
        }

        // This works as expected
        @Test
        fun `should fail when getting Second as First`() {
            expectCatching {
                returnValue = Second("foo")
                /**
                 * Fails as expected as [Second] cannot be cast to [First]
                 */
                getFirstViaReifiedReifiedChain()
            }.isFailure().isA<ClassCastException>()
        }
    }

    @Nested
    inner class ReifiedToNonReified {
        @Test
        fun `should succeed when getting First as First`() {
            expectCatching {
                returnValue = First("foo")
                getFirstViaReifiedNonReifiedChain()
            }.isSuccess().isA<First>()
        }

        // This fails unexpectedly
        @Test
        fun `should fail when getting Second as First`() {
            expectCatching {
                returnValue = Second("foo")
                /**
                 * Should fail as [Second] cannot be cast to [First]
                 * but actually just returns the [returnValue] as [Second],
                 * apparently ignoring the type cast to [First] and casting
                 * to [Any] instead
                 */
                getFirstViaReifiedNonReifiedChain()
            }.isFailure().isA<ClassCastException>()
        }
    }

    @Nested
    inner class NonReifiedToNonReified {
        @Test
        fun `should succeed when getting First as First`() {
            expectCatching {
                returnValue = First("foo")
                getFirstViaNonReifiedNonReifiedChain()
            }.isSuccess().isA<First>()
        }

        // This fails unexpectedly
        @Test
        fun `should fail when getting Second as First`() {
            expectCatching {
                returnValue = Second("foo")
                /**
                 * Should fail as [Second] cannot be cast to [First]
                 * but actually just returns the [returnValue] as [Second],
                 * apparently ignoring the type cast to [First] and casting
                 * to [Any] instead
                 */
                getFirstViaNonReifiedNonReifiedChain()
            }.isFailure().isA<ClassCastException>().and {
                get { message }.isEqualTo("foo")
            }
        }
    }
}