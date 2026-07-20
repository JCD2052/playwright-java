package org.jcd2052.core.elements;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.browser.services.interfaces.IElementSupplier;
import org.jcd2052.core.elements.selector.Selector;
import org.jcd2052.core.elements.interfaces.IElement;
import org.jcd2052.core.elements.interfaces.IElementCollection;

import java.util.ArrayList;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * A dynamic collection of web elements of a specific type.
 * <p>This class implements {@link IElementCollection} to manage a group of elements matching
 * a single Selector locator strategy. It handles waiting for specific collection states (e.g., waiting for at least
 * one element to appear) and dynamically wrapping the raw Playwright locators into framework-specific
 * custom elements (e.g., turning a list of button locators into a List of {@code IButtonElement}).</p>
 *
 * @param <T> The specific type of {@link IElement} contained in this collection.
 */
public class ElementCollection<T extends IElement> implements IElementCollection<T> {
    private final Selector selector;
    private final String name;
    private final IElementSupplier<T> supplier;
    private final IElementFactory elementFactory;
    private final ExpectedCount expectedCount;

    /**
     * Constructs a new {@code ElementCollection}.
     *
     * @param selector       The base Playwright Selector selector used to find all elements in the collection.
     * @param name           A base human-readable name for the collection, appended with an index for individual elements.
     * @param elementFactory The {@link IElementFactory} responsible for creating the individual elements.
     * @param supplier       The functional supplier template for instantiating the typed wrapper components.
     * @param expectedCount  The expectation rule applied before resolving the collection (e.g., MUST have elements, MUST be empty).
     */
    public ElementCollection(
            Selector selector,
            String name,
            IElementFactory elementFactory,
            IElementSupplier<T> supplier,
            ExpectedCount expectedCount) {
        this.selector = selector;
        this.name = name;
        this.supplier = supplier;
        this.elementFactory = elementFactory;
        this.expectedCount = expectedCount;
    }

    /**
     * Retrieves the list of resolved framework elements.
     * <p>This method first waits for the expected count condition to be met. It then determines
     * the total number of matching elements and iterates through them, using a deferred index-based
     * Selector wrapper to create isolated, individually addressable framework elements.</p>
     *
     * @return A {@link List} of instantiated elements of type {@code T}.
     */
    @Override
    public List<T> getElements() {
        waitForCondition();
        int size = getSize();
        List<T> elementList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            elementList.add(
                    supplier.get(
                            selector.nth(i),
                            String.format("%s %d", name, i + 1),
                            elementFactory
                    )
            );
        }
        return elementList;
    }

    /**
     * Evaluates the DOM to ensure the collection matches the {@link ExpectedCount} condition.
     * <p>Utilizes Playwright's native assertions and wait options to handle synchronization:</p>
     * <ul>
     * <li>{@code ZERO}: Asserts that the locator count is exactly 0.</li>
     * <li>{@code MORE_THAN_ZERO}: Waits for the first element to reach the {@code ATTACHED} state in the DOM.</li>
     * <li>{@code ANY}: Bypasses explicit waiting.</li>
     * </ul>
     */
    private void waitForCondition() {
        Locator locator = elementFactory.getElementFinderService().findElement(selector);
        switch (expectedCount) {
            case ZERO:
                assertThat(locator).hasCount(0);
                break;
            case MORE_THAN_ZERO:
                locator.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED));
                break;
            case ANY:
            default:
                break;
        }
    }

    /**
     * Retrieves the current number of elements in the DOM matching the collection's selector strategy.
     *
     * @return The integer count of matching elements.
     */
    private int getSize() {
        return elementFactory.getElementFinderService().findElement(selector).count();
    }
}