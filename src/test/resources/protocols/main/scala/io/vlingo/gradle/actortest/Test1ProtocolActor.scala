package io.vlingo.gradle.actortest

import io.vlingo.actors.Actor

import scala.beans.BeanProperty
import scala.collection.JavaConverters._

class Test1ProtocolActor extends Actor with Test1Protocol {

    override def doSomethingWith(name: String, texts: java.util.List[String]): Unit = {
        val builder = new StringBuilder()
        builder.append(name);
        for (text <- texts.asScala) {
            builder.append(text)
        }
        Test1ProtocolActor.doSomethingWith = builder.toString()
    }

    override def doSomethingElseWith(value: Int): Unit = {
        Test1ProtocolActor.doSomethingElseWithValue = value;
    }
}

object Test1ProtocolActor {
    @BeanProperty var doSomethingElseWithValue: Int = -1
    @BeanProperty var doSomethingWith: String = null
}
