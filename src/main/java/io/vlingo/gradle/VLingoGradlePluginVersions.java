package io.vlingo.gradle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


final class VLingoGradlePluginVersions {

    static String getDefaultVLingoVersion() throws IOException {
        Properties props = new Properties();
        try (InputStream input = VLingoGradlePluginVersions.class.getResourceAsStream("versions.properties")) {
            props.load(input);
        }
        return props.getProperty("default-vlingo-version");
    }

    private VLingoGradlePluginVersions() {
    }
}
