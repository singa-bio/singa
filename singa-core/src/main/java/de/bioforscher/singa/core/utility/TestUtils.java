package de.bioforscher.singa.core.utility;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * Access to commonly used test functions and constants.
 * Created by S on 29.09.2016.
 */
public class TestUtils {

    public static String getResourceAsFilepath(String filename) {
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        Objects.requireNonNull(ccl);
        URL resource = ccl.getResource(filename);
        Objects.requireNonNull(resource);
        // some a bit hacky way to ensure correct paths on windows (as some / will be added as prefix)
        return resource.getPath().replaceFirst("^/(.:/)", "$1");
    }

    public static InputStream getResourceAsStream(String filename) {
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        Objects.requireNonNull(ccl);
        InputStream is = ccl.getResourceAsStream(filename);
        return Objects.requireNonNull(is);
    }
}