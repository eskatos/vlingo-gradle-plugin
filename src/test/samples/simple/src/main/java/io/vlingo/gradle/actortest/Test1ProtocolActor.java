package io.vlingo.gradle.actortest;

import java.util.List;

import io.vlingo.actors.Actor;

public class Test1ProtocolActor extends Actor implements Test1Protocol {
    public static int doSomethingElseWithValue;
    public static String doSomethingWith;

    @Override
    public void doSomethingWith(String name, List<String> texts) {
        final StringBuilder builder = new StringBuilder();

        builder.append(name);

        for (final String text : texts) {
            builder.append(text);
        }

        doSomethingWith = builder.toString();
    }

    @Override
    public void doSomethingElseWith(int value) {
        doSomethingElseWithValue = value;
    }
}