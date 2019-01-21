package io.vlingo.gradle.actortest

import org.junit.Assert.assertEquals

import java.util.Arrays
import java.util.Collections

import org.junit.After
import org.junit.Before
import org.junit.Test

import io.vlingo.actors.Definition
import io.vlingo.actors.testkit.TestActor
import io.vlingo.actors.testkit.TestWorld

import scala.collection.JavaConverters._

class Test1ProtocolTest {
    private var testWorld: TestWorld = null

    @Test
    def testProtocolProxy(): Unit = {
        val test1 = testWorld.actorFor(classOf[Test1Protocol], new Definition(classOf[Test1ProtocolActor], Collections.emptyList()))

        test1.actor().doSomethingElseWith(1)
        assertEquals(1, Test1ProtocolActor.getDoSomethingElseWithValue())

        test1.actor().doSomethingWith("zero", List("one", "two", "three").asJava)
        assertEquals("zeroonetwothree", Test1ProtocolActor.getDoSomethingWith())
    }

    @Before
    def setUp(): Unit = {
        testWorld = TestWorld.start("test-proxy")
        Test1ProtocolActor.setDoSomethingElseWithValue(0)
        Test1ProtocolActor.setDoSomethingWith(null)
    }

    @After
    def tearDown(): Unit = {
        testWorld.terminate()
    }
}