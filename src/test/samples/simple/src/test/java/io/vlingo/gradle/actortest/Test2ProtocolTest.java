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
        final TestActor<Test2Protocol> test2 = testWorld.actorFor(new Definition(Test2ProtocolActor.class, Collections.emptyList()), Test2Protocol.class);

        test2.actor().doOneThing();
        assertEquals("doOneThing", Test2ProtocolActor.doOneThing);
    }

    @Test
    public void testDoAnotherThingUsing() {
        final TestActor<Test2Protocol> test2 = testWorld.actorFor(new Definition(Test2ProtocolActor.class, Collections.emptyList()), Test2Protocol.class);

        test2.actor().doAnotherThingUsing("hello", 1);
        assertEquals("doAnotherThingUsing: hello1", Test2ProtocolActor.doAnotherThingUsing);
    }

    @Test
    public void testSomethingRatherWonderful() {
        final TestActor<Test1Protocol> test1 = testWorld.actorFor(new Definition(Test1ProtocolActor.class, Collections.emptyList()), Test1Protocol.class);
        final TestActor<Test2Protocol> test2 = testWorld.actorFor(new Definition(Test2ProtocolActor.class, Collections.emptyList()), Test2Protocol.class);

        test2.actor().somethingRatherWonderful(test1.actor());
        assertEquals("somethingRatherWonderful: 1testingonetwothree", Test2ProtocolActor.somethingRatherWonderful);
    }

    @Before
    public void setUp() {
        testWorld = TestWorld.start("test-proxy");

        Test1ProtocolActor.doSomethingElseWithValue = 0;
        Test1ProtocolActor.doSomethingWith = null;

        Test2ProtocolActor.doAnotherThingUsing = null;
        Test2ProtocolActor.doOneThing = null;
        Test2ProtocolActor.somethingRatherWonderful = null;
    }

    @After
    public void tearDown() {
        testWorld.terminate();
    }
}