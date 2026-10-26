package io.github.jcd2052.elements.interfaces;

import java.io.File;
import java.nio.file.Path;

/**
 * A file upload field: an HTML {@code <input type="file">} element that files are set on directly, without an
 * operating-system file chooser.
 */
public interface IUploadBox extends IElement {
    /**
     * Sets the given file on the field, as if the user had chosen it.
     *
     * @param path The file to upload; it must exist on the machine running the tests.
     */
    void upload(Path path);

    /**
     * Sets the given file on the field, as if the user had chosen it.
     *
     * @param file The file to upload; it must exist on the machine running the tests.
     */
    default void upload(File file) {
        upload(file.toPath());
    }
}