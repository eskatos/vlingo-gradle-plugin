package io.vlingo.gradle.actortest;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.Definition;
import io.vlingo.actors.testkit.TestActor;
import io.vlingo.actors.testkit.TestWorld;

public class Test1ProtocolTest {
    private TestWorld testWorld;

    @Test
    public void testProtocolProxy() {
        final TestActor<Test1Protocol> test1 = testWorld.actorFor(new Definition(Test1ProtocolActor.class, Collections.emptyList()), Test1Protocol.class);

        test1.actor().doSomethingElseWith(1);
        assertEquals(1, Test1ProtocolActor.doSomethingElseWithValue);

        test1.actor().doSomethingWith("zero", Arrays.asList("one", "two", "three"));
        assertEquals("zeroonetwothree", Test1ProtocolActor.doSomethingWith);
    }

    @Before
    public void setUp() {
        testWorld = TestWorld.start("test-proxy");
        Test1ProtocolActor.doSomethingElseWithValue = 0;
        Test1ProtocolActor.doSomethingWith = null;
    }

    @After
    public void tearDown() {
        testWorld.terminate();
    }
}