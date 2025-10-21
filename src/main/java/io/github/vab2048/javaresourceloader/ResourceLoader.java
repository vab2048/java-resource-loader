package io.github.vab2048.javaresourceloader;

import lombok.SneakyThrows;

import org.apache.commons.io.IOUtils;

import java.io.File;

import java.io.InputStream;

import java.net.URL;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import java.nio.file.Paths;


/**
 * Loads resources.
 *
 * Notes:
 *
 * getClass().getResourcesAsStream(String resourcePath):
 * - If the resourcePath starts with "/" then it is viewed as an absolute path relative to the root of the classpath.
 * - Otherwise it is relative to the location of the class.
 * - e.g. For a class: a.b.SomeClass*, resourcePath of "c/d/file.txt" would resolve to "a/b/c/d/file. txt"
 * - The path is resolved to an absolute one and under the hood will delegate to the class loader to load the resource.
 * 
 * getClass().getClassLoader().getResourceAsStream(String resourcePath) :
 * - If your resourcePath starts with a "/", or it cannot find the resource: it will return nul.
 * - It will always be interpreted as an absolute resource path.
 */

public class ResourceLoader {

    @SneakyThrows
    public File getResource(String absoluteResourcePath) {
        URL url = getClass().getResource(ensurePathHasLeadingSlash(absoluteResourcePath));
        if (url == null) {
            throw new IllegalArgumentException("Resource path provided resolves to null: '%s'".formatted(absoluteResourcePath));
        }
        Path path = Paths.get(url.toURI());
        return path.toFile();
    }

    @SneakyThrows
    public String getResourceAsString(String absoluteResourcePath) {
        try (InputStream stream = getClass().getResourceAsStream(ensurePathHasLeadingSlash(absoluteResourcePath))) {
            if (stream == null) {
                throw new IllegalArgumentException("Resource path provided resolves to null: '%s'".formatted(absoluteResourcePath));
            }
            return IOUtils.toString(stream, StandardCharsets.UTF_8);
        }
    }

    public InputStream getResourceAsInputStream(String absoluteResourcePath) {
        return getClass().getResourceAsStream(ensurePathHasLeadingSlash(absoluteResourcePath));
    }

    public InputStream getResourceAsInputStream(Path absoluteResourcePath) {
        return getResourceAsInputStream(absoluteResourcePath.toString());
    }

    public URL getResourceURL(String absoluteResourcePath) {
        return getClass().getResource(ensurePathHasLeadingSlash(absoluteResourcePath));
    }

    private String ensurePathHasLeadingSlash(String resourcesPath) {
        return resourcesPath.startsWith("/") ? resourcesPath : "/" + resourcesPath;
    }
}
