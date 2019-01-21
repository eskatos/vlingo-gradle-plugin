package io.vlingo.gradle.actortest

interface Test1Protocol {
    fun doSomethingWith(name: String, texts: List<String>)
    fun doSomethingElseWith(value: Int)
}
