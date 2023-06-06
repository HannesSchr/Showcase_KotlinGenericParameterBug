// Actually nothing to inline in the non-reified functions, but here to keep the code as similar as possible.
// Explicit generic parameters to make it more apparent that the same type is passed along the chain.
@file:Suppress("NOTHING_TO_INLINE", "RemoveExplicitTypeArguments")

package com.example.inference

/**
 * Simple base class (can also be open or abstract, no change in the behavior)
 * with two implementing classes.
 */
sealed class Base(
    open val someField: String,
)

data class First(
    override val someField: String,
) : Base(
    someField
)

data class Second(
    override val someField: String,
) : Base(
    someField
)

/**
 * Functions that wrap different call chains with regard to
 * the reification of the generic parameter along the chain.
 *
 * Set the passed generic parameter to [First] for simple testing.
 */
// reified T to reified T
fun getFirstViaReifiedReifiedChain(): Base {
    return delegateReifiedTypeToReifiedType<First>()
}

// reified T to non-reified T
fun getFirstViaReifiedNonReifiedChain(): Base {
    return delegateReifiedTypeToNonReifiedType<First>()
}

// non-reified T to non-reified T
fun getFirstViaNonReifiedNonReifiedChain(): Base {
    return delegateNonReifiedTypeToNonReifiedType<First>()
}

// non-reified T to reified T is forbidden, so nothing here

/**
 * functions that delegate [T] as needed to the next generic function
 */
inline fun <reified T : Base> delegateReifiedTypeToReifiedType(): T =
    castValueToReifiedType<T>()

inline fun <reified T : Base> delegateReifiedTypeToNonReifiedType(): T =
    castValueToNonReifiedType<T>()

inline fun <T : Base> delegateNonReifiedTypeToNonReifiedType(): T =
    castValueToNonReifiedType<T>()

/**
 * Hard-cast the setup [returnValue] to the generic type.
 * This should fail if the [returnValue] is not castable to [T].
 */
// reified [T] stays the passed [First]
inline fun <reified T : Any> castValueToReifiedType(): T =
    returnValue as T

// non-reified [T] becomes its upper bound [Any]
// instead of respecting the passed parameter as [First]
inline fun <T : Any> castValueToNonReifiedType(): T {
    // none of these casts fail as [T] appears to become [Any]
    listOf(
        returnValue as T,
        "foobar" as T,
        13 as T,
        true as T,
        object {} as T,
    )

    return returnValue as T
}

/**
 * Simulate a generic return value
 */
lateinit var returnValue: Base
