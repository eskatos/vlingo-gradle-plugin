package io.vlingo.gradle.actortest

import io.vlingo.actors.Actor

class Test2ProtocolActor extends Actor implements Test2Protocol {

    private static String doOneThing
    static String getDoOneThing() { doOneThing }
    static void setDoOneThing(String value) { doOneThing = value }

    private static String doAnotherThingUsing
    static String getDoAnotherThingUsing() { doAnotherThingUsing }
    static void setDoAnotherThingUsing(String value) { doAnotherThingUsing = value }

    private static String somethingRatherWonderful
    static String getSomethingRatherWonderful() { somethingRatherWonderful }
    static void setSomethingRatherWonderful(String value) { somethingRatherWonderful = value }

    @Override
    void doOneThing() {
        doOneThing = "doOneThing"
    }

    @Override
    void doAnotherThingUsing(String text, int value) {
        doAnotherThingUsing = "doAnotherThingUsing: $text$value"
    }

    @Override
    void somethingRatherWonderful(final Test1Protocol test1) {
        test1.doSomethingElseWith(1)
        test1.doSomethingWith("testing", ["one", "two", "three"])
        somethingRatherWonderful = "somethingRatherWonderful: " + Test1ProtocolActor.doSomethingElseWithValue + Test1ProtocolActor.doSomethingWith
    }
}