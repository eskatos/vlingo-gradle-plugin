package io.vlingo.gradle

import org.gradle.util.GradleVersion


private
object GradleVersions {
    val current = GradleVersion.current()
    val v5_0 = GradleVersion.version("5.0")
    val v4_10 = GradleVersion.version("4.10")
    val v4_9 = GradleVersion.version("4.9")
}


internal
val isGradleFiveDotZeroOrGreater =
        GradleVersions.current >= GradleVersions.v5_0


internal
val isGradleFourDotTenOrGreater =
        GradleVersions.current >= GradleVersions.v4_10


internal
val isGradleFourDotNineOrGreater =
        GradleVersions.current >= GradleVersions.v4_9
