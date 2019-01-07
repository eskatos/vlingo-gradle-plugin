package io.vlingo.gradle.actortest;

import java.util.Arrays;

import io.vlingo.actors.Actor;

public class Test2ProtocolActor extends Actor implements Test2Protocol {
    public static String doOneThing;
    public static String doAnotherThingUsing;
    public static String somethingRatherWonderful;

    @Override
    public void doOneThing() {
        doOneThing = "doOneThing";
    }

    @Override
    public void doAnotherThingUsing(final String text, final int value) {
        doAnotherThingUsing = "doAnotherThingUsing: " + text + value;
    }

    @Override
    public void somethingRatherWonderful(final Test1Protocol test1) {
        test1.doSomethingElseWith(1);
        test1.doSomethingWith("testing", Arrays.asList("one", "two", "three"));
        somethingRatherWonderful = "somethingRatherWonderful: " + Test1ProtocolActor.doSomethingElseWithValue + Test1ProtocolActor.doSomethingWith;
    }
}