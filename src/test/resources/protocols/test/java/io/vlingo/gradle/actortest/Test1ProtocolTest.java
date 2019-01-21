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
        final TestActor<Test1Protocol> test1 = testWorld.actorFor(Test1Protocol.class, new Definition(Test1ProtocolActor.class, Collections.emptyList()));

        test1.actor().doSomethingElseWith(1);
        assertEquals(new Integer(1), (Integer) Test1ProtocolActor.getDoSomethingElseWithValue());

        test1.actor().doSomethingWith("zero", Arrays.asList("one", "two", "three"));
        assertEquals("zeroonetwothree", Test1ProtocolActor.getDoSomethingWith());
    }

    @Before
    public void setUp() {
        testWorld = TestWorld.start("test-proxy");
        Test1ProtocolActor.setDoSomethingElseWithValue(0);
        Test1ProtocolActor.setDoSomethingWith(null);
    }

    @After
    public void tearDown() {
        testWorld.terminate();
    }
}