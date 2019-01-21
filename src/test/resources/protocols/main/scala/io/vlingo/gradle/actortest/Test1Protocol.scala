package io.vlingo.gradle.actortest

trait Test1Protocol {
    def doSomethingWith(name: String, texts: java.util.List[String]): Unit
    def doSomethingElseWith(value: Int): Unit
}
