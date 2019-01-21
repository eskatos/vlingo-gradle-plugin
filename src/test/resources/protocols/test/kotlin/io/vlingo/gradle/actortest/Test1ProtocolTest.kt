package io.vlingo.gradle.actortest

import org.junit.Assert.assertEquals

import org.junit.After
import org.junit.Before
import org.junit.Test

import io.vlingo.actors.Definition
import io.vlingo.actors.testkit.TestActor
import io.vlingo.actors.testkit.TestWorld

class Test1ProtocolTest {
    private lateinit var testWorld: TestWorld

    @Test
    fun testProtocolProxy() {
        val test1 = testWorld.actorFor(Test1Protocol::class.java, Definition(Test1ProtocolActor::class.java, emptyList()))

        test1.actor().doSomethingElseWith(1)
        assertEquals(1, Test1ProtocolActor.getDoSomethingElseWithValue())

        test1.actor().doSomethingWith("zero", listOf("one", "two", "three"))
        assertEquals("zeroonetwothree", Test1ProtocolActor.getDoSomethingWith())
    }

    @Before
    fun setUp() {
        testWorld = TestWorld.start("test-proxy")
        Test1ProtocolActor.setDoSomethingElseWithValue(0)
        Test1ProtocolActor.setDoSomethingWith(null)
    }

    @After
    fun tearDown() {
        testWorld.terminate()
    }
}