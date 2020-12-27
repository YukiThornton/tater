package project.tater_e2e.helper

import org.amshove.kluent.should

data class AssertableJson(private val jsonMap: Map<Any, Any>) {
    fun shouldHaveValueOf(keys: String, expected: String) = should("The value of key \"$keys\" should be \"$expected\", but it was ${getValueOf(keys)?.let{ "\"$it\"" } ?: "null"} in json ${this.jsonMap}") {
        this.getValueOf(keys) == expected
    }

    fun shouldHaveValueOf(keys: String, expected: Int) = should("The value of key \"$keys\" should be $expected, but it was ${getIntValueOf(keys)} in json ${this.jsonMap}") {
        this.getIntValueOf(keys) == expected
    }

    fun shouldHaveValueOf(keys: String, expected: Double) = should("The value of key \"$keys\" should be $expected, but it was ${getDoubleValueOf(keys)} in json ${this.jsonMap}") {
        this.getDoubleValueOf(keys) == expected
    }

    fun shouldHaveValueOf(keys: String, expected: Boolean) = should("The value of key \"$keys\" should be $expected, but it was ${getBooleanValueOf(keys)} in json ${this.jsonMap}") {
        this.getBooleanValueOf(keys) == expected
    }

    fun shouldHaveExpectedAmountOf(keys: String, expectedAmount: Int) = should("Element amount of key \"$keys\" should be $expectedAmount, but it was ${elementCountOf(keys)} in json $jsonMap") {
        elementCountOf(keys) ==  expectedAmount
    }

    private fun elementCountOf(keys: String): Int {
        if (jsonMap.isEmpty()) return 0
        val targetList = getRecursively(jsonMap, keys.split(".")) as List<*>
        return targetList.size
    }

    private fun getValueOf(keys: String): String? {
        if (jsonMap.isEmpty()) return null
        return getRecursively(jsonMap, keys.split(".")) as String?
    }

    private fun getIntValueOf(keys: String): Int? {
        if (jsonMap.isEmpty()) return null
        return getRecursively(jsonMap, keys.split(".")) as Int?
    }

    private fun getDoubleValueOf(keys: String): Double? {
        if (jsonMap.isEmpty()) return null
        return getRecursively(jsonMap, keys.split(".")) as Double?
    }

    private fun getBooleanValueOf(keys: String): Boolean? {
        if (jsonMap.isEmpty()) return null
        return getRecursively(jsonMap, keys.split(".")) as Boolean?
    }

    private fun getRecursively(target: Any?, keys: List<String>): Any? {
        if (keys.isEmpty()) return target
        return when (target) {
            is List<*> -> getRecursively(target[keys.first().toInt()], keys.subList(1, keys.size))
            is Map<*, *> -> getRecursively(target[keys.first()], keys.subList(1, keys.size))
            else -> throw RuntimeException("Could not get value of $keys from $target")
        }
    }
}