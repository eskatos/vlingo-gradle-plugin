package io.vlingo.gradle.actortest

import org.junit.Assert.assertEquals

import org.junit.After
import org.junit.Before
import org.junit.Test

import io.vlingo.actors.Definition
import io.vlingo.actors.testkit.TestActor
import io.vlingo.actors.testkit.TestWorld

class Test2ProtocolTest {
    private lateinit var testWorld: TestWorld

    @Test
    fun testDoOneThing() {
        val test2 = testWorld.actorFor(Test2Protocol::class.java, Definition(Test2ProtocolActor::class.java, emptyList()))

        test2.actor().doOneThing()
        assertEquals("doOneThing", Test2ProtocolActor.getDoOneThing())
    }

    @Test
    fun testDoAnotherThingUsing() {
        val test2 = testWorld.actorFor(Test2Protocol::class.java, Definition(Test2ProtocolActor::class.java, emptyList()))

        test2.actor().doAnotherThingUsing("hello", 1)
        assertEquals("doAnotherThingUsing: hello1", Test2ProtocolActor.getDoAnotherThingUsing())
    }

    @Test
    fun testSomethingRatherWonderful() {
        val test1 = testWorld.actorFor(Test1Protocol::class.java, Definition(Test1ProtocolActor::class.java, emptyList()))
        val test2 = testWorld.actorFor(Test2Protocol::class.java, Definition(Test2ProtocolActor::class.java, emptyList()))

        test2.actor().somethingRatherWonderful(test1.actor())
        assertEquals("somethingRatherWonderful: 1testingonetwothree", Test2ProtocolActor.getSomethingRatherWonderful())
    }

    @Before
    fun setUp() {
        testWorld = TestWorld.start("test-proxy")

        Test1ProtocolActor.setDoSomethingElseWithValue(0)
        Test1ProtocolActor.setDoSomethingWith(null)

        Test2ProtocolActor.setDoAnotherThingUsing(null)
        Test2ProtocolActor.setDoOneThing(null)
        Test2ProtocolActor.setSomethingRatherWonderful(null)
    }

    @After
    fun tearDown() {
        testWorld.terminate()
    }
}
