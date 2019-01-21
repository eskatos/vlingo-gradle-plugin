@file:JvmName("Test1ProtocolActor")
package io.vlingo.gradle.actortest;

import io.vlingo.actors.Actor

class Test1ProtocolActor : Actor(), Test1Protocol {

    companion object {

        private var doSomethingElseWithValue: Int? = null
        @JvmStatic fun getDoSomethingElseWithValue(): Int? = doSomethingElseWithValue
        @JvmStatic fun setDoSomethingElseWithValue(value: Int?) { doSomethingElseWithValue = value }

        private var doSomethingWith: String? = null
        @JvmStatic fun getDoSomethingWith() = doSomethingWith
        @JvmStatic fun setDoSomethingWith(value: String?) { doSomethingWith = value }
    }

    override fun doSomethingWith(name: String, texts: List<String>) {
        val builder = StringBuilder()

        builder.append(name)

        for (text in texts) {
            builder.append(text)
        }

        doSomethingWith = builder.toString();
    }

    override fun doSomethingElseWith(value: Int) {
        doSomethingElseWithValue = value
    }
}
