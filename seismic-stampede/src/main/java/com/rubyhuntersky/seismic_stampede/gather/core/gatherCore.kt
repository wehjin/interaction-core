package com.rubyhuntersky.seismic_stampede.gather.core

fun gatherOf(
    label: String,
    options: List<GatherOption> = emptyList(),
    validator: (value: String, label: String, otherValues: Map<String, String>) -> GatherValidity
) = Gather(label, options, validator)

data class Gather(
    val label: String,
    val options: List<GatherOption>,
    val validator: (value: String, label: String, gatheredValues: Map<String, String>) -> GatherValidity,
    val predecessor: Gather? = null
) {
    fun and(
        label: String,
        options: List<GatherOption> = emptyList(),
        validator: (value: String, label: String, otherValues: Map<String, String>) -> GatherValidity
    ): Gather = Gather(label, options, validator, this)

    val count: Int
        get() = predecessor?.let { it.count + 1 } ?: 1

    val first: Gather
        get() = predecessor?.first ?: this

    val all: List<Gather> by lazy { predecessor?.let { predecessor.all + this } ?: listOf(this) }

    fun validate(value: String, otherValues: Map<String, String>): GatherValidity {
        return validator(value, label, otherValues)
    }
}

sealed class GatherOption

sealed class GatherValidity {
    data class Valid(val validValue: String) : GatherValidity()
    data class Invalid(val reason: String) : GatherValidity()
}

fun validWhenNotEmpty(
    value: String,
    label: String,
    @Suppress("UNUSED_PARAMETER") otherValues: Map<String, String>
): GatherValidity {
    val trimmedValue = value.trim()
    return if (trimmedValue.isNotEmpty()) {
        GatherValidity.Valid(trimmedValue)
    } else {
        GatherValidity.Invalid("$label is required")
    }
}
