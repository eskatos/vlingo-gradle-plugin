@file:JvmName("Test2ProtocolActor")
package io.vlingo.gradle.actortest

import io.vlingo.actors.Actor

class Test2ProtocolActor : Actor(), Test2Protocol {

    companion object {

        private var doOneThing: String? = null
        @JvmStatic fun getDoOneThing() = doOneThing
        @JvmStatic fun setDoOneThing(value: String?) { doOneThing = value }

        private var doAnotherThingUsing: String? = null
        @JvmStatic fun getDoAnotherThingUsing() = doAnotherThingUsing
        @JvmStatic fun setDoAnotherThingUsing(value: String?) { doAnotherThingUsing = value }

        private var somethingRatherWonderful: String? = null
        @JvmStatic fun getSomethingRatherWonderful() = somethingRatherWonderful
        @JvmStatic fun setSomethingRatherWonderful(value: String?) { somethingRatherWonderful = value }
    }

    override fun doOneThing() {
        doOneThing = "doOneThing";
    }

    override fun doAnotherThingUsing(text: String, value: Int) {
        doAnotherThingUsing = "doAnotherThingUsing: $text$value";
    }

    override fun somethingRatherWonderful(test1: Test1Protocol) {
        test1.doSomethingElseWith(1);
        test1.doSomethingWith("testing", listOf("one", "two", "three"));
        somethingRatherWonderful = "somethingRatherWonderful: " + Test1ProtocolActor.getDoSomethingElseWithValue() + Test1ProtocolActor.getDoSomethingWith()
    }
}
