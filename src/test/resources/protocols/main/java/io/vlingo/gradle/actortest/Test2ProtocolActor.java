package io.vlingo.gradle.actortest;

import java.util.Arrays;

import io.vlingo.actors.Actor;

public class Test2ProtocolActor extends Actor implements Test2Protocol {

    private static String doOneThing;
    public static String getDoOneThing() { return doOneThing; }
    public static void setDoOneThing(String value) { doOneThing = value; }

    private static String doAnotherThingUsing;
    public static String getDoAnotherThingUsing() { return doAnotherThingUsing; }
    public static void setDoAnotherThingUsing(String value) { doAnotherThingUsing = value; }

    private static String somethingRatherWonderful;
    public static String getSomethingRatherWonderful() { return somethingRatherWonderful; }
    public static void setSomethingRatherWonderful(String value) { somethingRatherWonderful = value; }

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
        somethingRatherWonderful = "somethingRatherWonderful: " + Test1ProtocolActor.getDoSomethingElseWithValue() + Test1ProtocolActor.getDoSomethingWith();
    }
}
