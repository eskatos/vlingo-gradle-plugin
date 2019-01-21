package io.vlingo.gradle.actortest

interface Test2Protocol {
    fun doOneThing()
    fun doAnotherThingUsing(text: String, value: Int)
    fun somethingRatherWonderful(test1: Test1Protocol)
}
