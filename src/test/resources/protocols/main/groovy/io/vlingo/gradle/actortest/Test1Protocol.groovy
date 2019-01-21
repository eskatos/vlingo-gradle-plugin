package io.vlingo.gradle.actortest

interface Test1Protocol {
    void doSomethingWith(String name, List<String> texts)
    void doSomethingElseWith(int value)
}