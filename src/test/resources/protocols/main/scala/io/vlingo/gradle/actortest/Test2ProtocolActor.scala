package io.vlingo.gradle.actortest

import io.vlingo.actors.Actor

import scala.beans.BeanProperty
import scala.collection.JavaConverters._

class Test2ProtocolActor extends Actor with Test2Protocol {

    override def doOneThing(): Unit = {
        Test2ProtocolActor.doOneThing = "doOneThing"
    }

    override def doAnotherThingUsing(text: String, value: Int): Unit = {
        Test2ProtocolActor.doAnotherThingUsing = "doAnotherThingUsing: " + text + value
    }

    override def somethingRatherWonderful(test1: Test1Protocol): Unit = {
        test1.doSomethingElseWith(1)
        test1.doSomethingWith("testing", List("one", "two", "three").asJava)
        Test2ProtocolActor.somethingRatherWonderful = "somethingRatherWonderful: " + Test1ProtocolActor.doSomethingElseWithValue + Test1ProtocolActor.doSomethingWith
    }
}

object Test2ProtocolActor {
    @BeanProperty var doOneThing: String = null
    @BeanProperty var doAnotherThingUsing: String = null
    @BeanProperty var somethingRatherWonderful: String = null
}
