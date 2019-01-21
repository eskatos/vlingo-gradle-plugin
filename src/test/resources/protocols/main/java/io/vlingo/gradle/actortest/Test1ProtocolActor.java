package io.vlingo.gradle.actortest;

import java.util.List;

import io.vlingo.actors.Actor;

public class Test1ProtocolActor extends Actor implements Test1Protocol {

    private static int doSomethingElseWithValue;
    public static int getDoSomethingElseWithValue() { return doSomethingElseWithValue; }
    public static void setDoSomethingElseWithValue(int value) { doSomethingElseWithValue = value; }

    private static String doSomethingWith;
    public static String getDoSomethingWith() { return doSomethingWith; }
    public static void setDoSomethingWith(String value) { doSomethingWith = value; }

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