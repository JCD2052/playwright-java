package org.jcd2052.core.elements.interfaces;

import java.io.File;

/**
 * Represents a web element capable of handling file uploads.
 * <p>
 * Extending the foundational {@link IElement}, this interface provides a dedicated contract
 * for interacting with file input fields or custom upload dropzones. It abstracts away
 * the underlying browser-level interactions required to successfully attach a local file
 * to the web application.
 * </p>
 */
public interface IUploadBox extends IElement {
    /**
     * Uploads the specified local file to the associated web element.
     * <p>
     * Depending on the specific implementation, this method may inject the file path directly
     * into a standard {@code <input type="file">} element, or it may utilize Playwright's
     * advanced event interception to handle modern, custom OS-level file chooser dialogs.
     * </p>
     *
     * @param file The {@link File} object representing the local file to be uploaded.
     */
    void upload(File file);
}