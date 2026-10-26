package io.github.jcd2052.elements;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.github.jcd2052.browser.services.interfaces.IElementFactory;
import io.github.jcd2052.browser.services.interfaces.IElementSupplier;
import io.github.jcd2052.elements.selector.Selector;
import io.github.jcd2052.elements.interfaces.IElement;
import io.github.jcd2052.elements.interfaces.IElementCollection;
import io.github.jcd2052.exceptions.PlaywrightExceptionParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Default {@link IElementCollection}: the elements matching one selector, read each time
 * {@link #getElements()} is called.
 * <p>
 * Reading first waits for the {@link ExpectedCount}, then creates one element per match. Each element is bound
 * to its position ({@code selector.nth(i)}), not to a specific node: if the page changes, element {@code i}
 * refers to whatever is then at position {@code i}, so read the collection again after the page changes.
 * </p>
 *
 * @param <T> The type of the collection's elements.
 */
public class ElementCollection<T extends IElement> implements IElementCollection<T> {
    private final Selector selector;
    private final String name;
    private final IElementSupplier<T> supplier;
    private final IElementFactory elementFactory;
    private final ExpectedCount expectedCount;

    /**
     * Creates a collection.
     *
     * @param selector       The selector matching every element of the collection.
     * @param name           The collection's name; each element is named after it with its position, e.g. "Tag 1".
     * @param elementFactory The factory the elements are created with.
     * @param supplier       Creates each element.
     * @param expectedCount  The count condition to wait for before the elements are read.
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
     * Waits for the expected count, then creates one element per current match, each bound to its position.
     *
     * @return The elements, in page order.
     * @throws io.github.jcd2052.exceptions.BasePlaywrightException if the expected count is not reached in time.
     */
    @Override
    public List<T> getElements() {
        waitForCondition();
        int size = getLocator().count();
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
     * Waits, up to the window's default timeout, for the collection's {@link ExpectedCount}:
     * <ul>
     * <li>{@code ZERO}: Waits until no matching element remains attached to the DOM.</li>
     * <li>{@code MORE_THAN_ZERO}: Waits for the first element to reach the {@code ATTACHED} state in the DOM.</li>
     * <li>{@code ANY}: Bypasses explicit waiting.</li>
     * </ul>
     *
     * @throws io.github.jcd2052.exceptions.BasePlaywrightException if the condition is not met in time.
     */
    private void waitForCondition() {
        WaitForSelectorState state;
        switch (expectedCount) {
            case ZERO:
                state = WaitForSelectorState.DETACHED;
                break;
            case MORE_THAN_ZERO:
                state = WaitForSelectorState.ATTACHED;
                break;
            case ANY:
            default:
                return;
        }

        try {
            getLocator().first().waitFor(new Locator.WaitForOptions().setState(state));
        } catch (PlaywrightException e) {
            throw PlaywrightExceptionParser.parse(e, name, selector, "wait for " + expectedCount + " elements");
        }
    }

    private Locator getLocator() {
        return elementFactory.getElementFinderService().findElement(selector);
    }
}