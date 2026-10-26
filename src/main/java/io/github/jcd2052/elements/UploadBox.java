package io.github.jcd2052.elements;

import io.github.jcd2052.browser.services.interfaces.IElementFactory;
import io.github.jcd2052.elements.selector.Selector;
import io.github.jcd2052.elements.interfaces.IUploadBox;
import io.github.jcd2052.logger.i18n.LogAction;

import java.nio.file.Path;

/**
 * The built-in {@link IUploadBox}, for an HTML {@code <input type="file">} element.
 * <p>
 * Files are set on the input directly with Playwright's {@code setInputFiles()}, so no operating-system file
 * chooser opens. The selector must match the {@code <input>} itself, not a styled button around it.
 * </p>
 */
public class UploadBox extends AbstractElement implements IUploadBox {
    /**
     * Constructs a new {@code UploadBox} element.
     *
     * @param selector       The selector of the {@code <input type="file">} element.
     * @param name           The element\'s name, used in log messages and errors.
     * @param elementFactory The factory creating the element.
     */
    protected UploadBox(Selector selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }

    /**
     * Sets the given file on the input, as if the user had chosen it.
     *
     * @param filePath The file to upload; it must exist on the machine running the tests.
     */
    @Override
    public void upload(Path filePath) {
        executeAction(
                () -> getLocator().setInputFiles(filePath),
                LogAction.FILE_UPLOADED, filePath.getFileName().toString());
    }
}