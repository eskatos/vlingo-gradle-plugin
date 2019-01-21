package io.vlingo.gradle

import org.gradle.util.GradleVersion


private
val gradleFiveDotZero = GradleVersion.version("5.0")


private
val currentGradleVersion = GradleVersion.current()


internal
val isGradleFiveDotZeroOrGreater = currentGradleVersion >= gradleFiveDotZero