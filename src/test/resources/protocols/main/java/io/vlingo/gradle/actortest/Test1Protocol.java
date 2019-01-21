package io.vlingo.gradle.actortest;

import java.util.List;

public interface Test1Protocol {
    void doSomethingWith(final String name, final List<String> texts);
    void doSomethingElseWith(final int value);
}