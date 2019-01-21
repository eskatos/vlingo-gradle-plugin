package io.vlingo.gradle.actortest

import org.junit.Assert.assertEquals

import java.util.Collections

import org.junit.After
import org.junit.Before
import org.junit.Test

import io.vlingo.actors.Definition
import io.vlingo.actors.testkit.TestActor
import io.vlingo.actors.testkit.TestWorld

class Test2ProtocolTest {
    private var testWorld: TestWorld = null

    @Test
    def testDoOneThing(): Unit = {
        val test2 = testWorld.actorFor(classOf[Test2Protocol], new Definition(classOf[Test2ProtocolActor], Collections.emptyList()))

        test2.actor().doOneThing()
        assertEquals("doOneThing", Test2ProtocolActor.getDoOneThing())
    }

    @Test
    def testDoAnotherThingUsing(): Unit = {
        val test2 = testWorld.actorFor(classOf[Test2Protocol], new Definition(classOf[Test2ProtocolActor], Collections.emptyList()))

        test2.actor().doAnotherThingUsing("hello", 1)
        assertEquals("doAnotherThingUsing: hello1", Test2ProtocolActor.getDoAnotherThingUsing())
    }

    @Test
    def testSomethingRatherWonderful(): Unit = {
        val test1 = testWorld.actorFor(classOf[Test1Protocol], new Definition(classOf[Test1ProtocolActor], Collections.emptyList()))
        val test2 = testWorld.actorFor(classOf[Test2Protocol], new Definition(classOf[Test2ProtocolActor], Collections.emptyList()))

        test2.actor().somethingRatherWonderful(test1.actor())
        assertEquals("somethingRatherWonderful: 1testingonetwothree", Test2ProtocolActor.getSomethingRatherWonderful())
    }

    @Before
    def setUp(): Unit = {
        testWorld = TestWorld.start("test-proxy")

        Test1ProtocolActor.setDoSomethingElseWithValue(0)
        Test1ProtocolActor.setDoSomethingWith(null)

        Test2ProtocolActor.setDoAnotherThingUsing(null)
        Test2ProtocolActor.setDoOneThing(null)
        Test2ProtocolActor.setSomethingRatherWonderful(null)
    }

    @After
    def tearDown(): Unit = {
        testWorld.terminate()
    }
}
