package io.vlingo.gradle.actortest

import io.vlingo.actors.Actor

class Test1ProtocolActor extends Actor implements Test1Protocol {

    private static int doSomethingElseWithValue
    static int getDoSomethingElseWithValue() { doSomethingElseWithValue }
    static void setDoSomethingElseWithValue(int value) { doSomethingElseWithValue = value }

    private static String doSomethingWith
    static String getDoSomethingWith() { doSomethingWith }
    static void setDoSomethingWith(String value) { doSomethingWith = value }

    @Override
    void doSomethingWith(String name, List<String> texts) {
        final StringBuilder builder = new StringBuilder()

        builder.append(name)

        for (final String text : texts) {
            builder.append(text)
        }

        doSomethingWith = builder.toString()
    }

    @Override
    void doSomethingElseWith(int value) {
        doSomethingElseWithValue = value
    }
}