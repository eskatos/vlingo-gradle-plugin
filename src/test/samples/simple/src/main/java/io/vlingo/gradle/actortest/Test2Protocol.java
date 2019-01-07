package io.vlingo.gradle.actortest;

public interface Test2Protocol {
    void doOneThing();
    void doAnotherThingUsing(final String text, final int value);
    void somethingRatherWonderful(final Test1Protocol test1);
}