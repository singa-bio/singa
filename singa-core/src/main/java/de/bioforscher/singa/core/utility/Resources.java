package de.bioforscher.singa.core.utility;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * Provides access to test and project and test resources.
 *
 * @author sb
 */
public class Resources {

    /**
     * Returns the file path to the resource in the given directory relative from the resources folder.
     *
     * @param resourceLocation The location of the resource (relative from the resource folder).
     * @return The qualified location of the file.
     */
    public static String getResourceAsFileLocation(String resourceLocation) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Objects.requireNonNull(contextClassLoader);
        URL resource = contextClassLoader.getResource(resourceLocation);
        Objects.requireNonNull(resource);
        // some a bit hacky way to ensure correct paths on windows (as some / will be added as prefix)
        return resource.getPath().replaceFirst("^/(.:/)", "$1");
    }

    /**
     * Returns the resource as a InputStream, this is the most save way to get resources independently from packaging.
     *
     * @param resourceLocation The location of the resource (relative from the resource folder).
     * @return The resource as a InputStream.
     */
    public static InputStream getResourceAsStream(String resourceLocation) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Objects.requireNonNull(contextClassLoader);
        InputStream inputStream = contextClassLoader.getResourceAsStream(resourceLocation);
        return Objects.requireNonNull(inputStream);
    }
}