package io.vlingo.gradle.actortest

trait Test2Protocol {
    def doOneThing(): Unit
    def doAnotherThingUsing(text: String, value: Int): Unit
    def somethingRatherWonderful(test1: Test1Protocol): Unit
}
