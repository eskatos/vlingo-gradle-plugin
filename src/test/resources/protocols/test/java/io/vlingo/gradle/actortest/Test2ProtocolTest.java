package io.vlingo.gradle.actortest;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.Definition;
import io.vlingo.actors.testkit.TestActor;
import io.vlingo.actors.testkit.TestWorld;

public class Test2ProtocolTest {
    private TestWorld testWorld;

    @Test
    public void testDoOneThing() {
        final TestActor<Test2Protocol> test2 = testWorld.actorFor(Test2Protocol.class, new Definition(Test2ProtocolActor.class, Collections.emptyList()));

        test2.actor().doOneThing();
        assertEquals("doOneThing", Test2ProtocolActor.getDoOneThing());
    }

    @Test
    public void testDoAnotherThingUsing() {
        final TestActor<Test2Protocol> test2 = testWorld.actorFor(Test2Protocol.class, new Definition(Test2ProtocolActor.class, Collections.emptyList()));

        test2.actor().doAnotherThingUsing("hello", 1);
        assertEquals("doAnotherThingUsing: hello1", Test2ProtocolActor.getDoAnotherThingUsing());
    }

    @Test
    public void testSomethingRatherWonderful() {
        final TestActor<Test1Protocol> test1 = testWorld.actorFor(Test1Protocol.class, new Definition(Test1ProtocolActor.class, Collections.emptyList()));
        final TestActor<Test2Protocol> test2 = testWorld.actorFor(Test2Protocol.class, new Definition(Test2ProtocolActor.class, Collections.emptyList()));

        test2.actor().somethingRatherWonderful(test1.actor());
        assertEquals("somethingRatherWonderful: 1testingonetwothree", Test2ProtocolActor.getSomethingRatherWonderful());
    }

    @Before
    public void setUp() {
        testWorld = TestWorld.start("test-proxy");

        Test1ProtocolActor.setDoSomethingElseWithValue(0);
        Test1ProtocolActor.setDoSomethingWith(null);

        Test2ProtocolActor.setDoAnotherThingUsing(null);
        Test2ProtocolActor.setDoOneThing(null);
        Test2ProtocolActor.setSomethingRatherWonderful(null);
    }

    @After
    public void tearDown() {
        testWorld.terminate();
    }
}