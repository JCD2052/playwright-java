package org.jcd2052.core.elements;

import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.elements.selector.Selector;
import org.jcd2052.core.elements.interfaces.IUploadBox;
import org.jcd2052.core.logger.LoggerProvider;

import java.io.File;
import java.nio.file.Paths;

/**
 * A concrete implementation of a file upload component.
 * <p>
 * This class utilizes Playwright's native {@code setInputFiles()} mechanism.
 * It is designed specifically to interact with standard HTML {@code <input type="file">}
 * elements in the DOM. It directly bypasses the Operating System's file chooser dialog
 * by injecting the file path straight into the browser's input node.
 * </p>
 */
public class UploadBox extends AbstractElement implements IUploadBox {
    /**
     * Constructs a new {@code UploadBox} element.
     *
     * @param selector       The Playwright Selector locator strategy used to find strictly targeting the {@code <input type="file">} element.
     * @param name           The human-readable name of the upload field, utilized for automated logging.
     * @param elementFactory The central factory used for dependency injection and element resolution.
     */
    protected UploadBox(Selector selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }

    /**
     * Uploads the specified local file directly into the input element.
     * <p>
     * Resolves the provided {@link File} object into a standard Java NIO {@link java.nio.file.Path}
     * and passes it directly to Playwright's locator engine.
     * </p>
     *
     * @param file The local file to be uploaded. Ensure the file exists on the execution machine's filesystem.
     */
    @Override
    public void upload(File file) {
        getLocator().setInputFiles(Paths.get(file.getPath()));
        LoggerProvider.getLogger().debugElementAction(
                getElementType(),
                getName(),
                "successfully uploaded file: '%s'",
                file.getName());
    }
}