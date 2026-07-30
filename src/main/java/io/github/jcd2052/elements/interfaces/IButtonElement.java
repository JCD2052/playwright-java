package io.github.jcd2052.elements.interfaces;

import io.github.jcd2052.browser.services.interfaces.IElementFactory;

/**
 * Represents a clickable button element on a web page.
 * <p>
 * This interface serves as a specialized type of {@link IElement}, allowing
 * the {@link IElementFactory}
 * to instantiate concrete button implementations and providing semantic clarity in Page Object models.
 */
public interface IButtonElement extends IElement {
}