package io.github.vab2048.javaresourceloader;

import lombok.EqualsAndHashCode;

import lombok.Getter;

import lombok.SneakyThrows;

import lombok.ToString;

import org.apache.commons.io.IOUtils;

import java.io.File;

import java.io.InputStream;

import java.net.URL;


import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import java.nio.file.Paths;

/**
 * Wrapper over an absolute resource path which points to a resource file.
 * It is "absolute" in the sense that the root is the same root as the root of the classpath.
 */
@ToString
@EqualsAndHashCode
public class ResourceFilePath {
    private static final ResourceLoader RESOURCE_LOADER = new ResourceLoader();
    private final String resourcePathAsString;

    @Getter
    private final URL resourceURL;

    @SneakyThrows
    public ResourceFilePath(String absoluteResourcePath) {
        this.resourcePathAsString = absoluteResourcePath;
        // Fail fast if we are unable to resolve the resource as a URL.
        this.resourceURL = asURLOrElseThrow();
    }

    private URL asURLOrElseThrow() {
        URL resourceURL = RESOURCE_LOADER.getResourceURL(resourcePathAsString);
        if (resourceURL == null) {
            throw new IllegalArgumentException("Resource path: '%s' is not resolvable as a URL.".formatted(resourcePathAsString));
        }
        return resourceURL;
    }

    @SneakyThrows
    public Path resolveSiblingPath(String toResolve) {
        Path parentPath = Path.of(resourceURL.toURI()).getParent();
        return parentPath.resolve(toResolve).normalize();
    }

    public ResourceFilePath resolveSiblingResourcePath(String toResolve) {
        Path parentResourcePath = Path.of(resourcePathAsString).getParent();
        return new ResourceFilePath(parentResourcePath.resolve(toResolve).normalize().toString());
    }

    public String getParentName() {
        // getParent() gets the Path of the whole parent. We get the "fileName" to get the directory name.
        return Path.of(resourcePathAsString).getParent().getFileName().toString();
    }

    public InputStream asInputStream() {
        return RESOURCE_LOADER.getResourceAsInputStream(resourcePathAsString);
    }

    @SneakyThrows
    public Path asPath() {
        // To work on both Windows and Linux, we get the path by converting URL -> URI -> Path.
        // This Path can then be converted to a file if needed.
        // See https://stackoverflow.com/a/17870390 for explanation of the need for doing this.
        return Paths.get(resourceURL.toURI());
    }

    public File asFile() {
        return asPath().toFile();
    }

    @SneakyThrows
    public String asString() {
        return IOUtils.toString(asInputStream(), StandardCharsets.UTF_8);
    }

    public String fileName() {
        return Paths.get(resourceURL.getPath()).getFileName().toString();
    }


}
