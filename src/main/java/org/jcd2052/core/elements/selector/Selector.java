package org.jcd2052.core.elements.selector;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * A Just-In-Time (JIT) locator strategy wrapper for Playwright.
 * Evaluates the selector against a Page or a parent Locator at the exact moment of action,
 * making it thread-safe and compatible with Singleton Page Objects.
 */
public abstract class Selector {
    /**
     * Resolves the locator from the root page.
     *
     * @param page the Playwright page instance
     * @return the evaluated Playwright Locator
     */
    public abstract Locator evaluate(Page page);

    /**
     * Resolves the locator from a parent element.
     *
     * @param parent the parent Playwright Locator instance
     * @return the evaluated child Playwright Locator
     */
    public abstract Locator evaluate(Locator parent);

    /**
     * Locates an element using a standard CSS or XPath query.
     *
     * @param selector the CSS or XPath query string
     * @return a new Selector instance for the strategy
     */
    public static Selector bySelector(String selector) {
        return of(page -> page.locator(selector), parent -> parent.locator(selector));
    }

    /**
     * Locates an element by its text content.
     *
     * @param text the text to search for
     * @return a new Selector instance for the text strategy
     */
    public static Selector byText(String text) {
        return of(page -> page.getByText(text), parent -> parent.getByText(text));
    }

    /**
     * Locates an element by its text content with exact matching configuration.
     *
     * @param text  the text to search for
     * @param exact true to match the exact text, false for substring matching
     * @return a new Selector instance for the text strategy
     */
    public static Selector byText(String text, boolean exact) {
        return of(
                page -> page.getByText(text, new Page.GetByTextOptions().setExact(exact)),
                parent -> parent.getByText(text, new Locator.GetByTextOptions().setExact(exact)));
    }

    /**
     * Locates an element by its text content using a regular expression.
     *
     * @param pattern the regular expression pattern
     * @return a new Selector instance for the text strategy
     */
    public static Selector byText(Pattern pattern) {
        return of(page -> page.getByText(pattern), parent -> parent.getByText(pattern));
    }

    /**
     * Locates an element by its ARIA role.
     *
     * @param role the ARIA role to search for
     * @return a new Selector instance for the role strategy
     */
    public static Selector byRole(AriaRole role) {
        return of(page -> page.getByRole(role), parent -> parent.getByRole(role));
    }

    /**
     * Locates an element by its ARIA role and accessible name.
     *
     * @param role the ARIA role to search for
     * @param name the accessible name of the element
     * @return a new Selector instance for the role strategy
     */
    public static Selector byRole(AriaRole role, String name) {
        return of(
                page -> page.getByRole(role, new Page.GetByRoleOptions().setName(name)),
                parent -> parent.getByRole(role, new Locator.GetByRoleOptions().setName(name)));
    }

    /**
     * Locates an element by its ARIA role with advanced configuration options.
     *
     * @param role    the ARIA role to search for
     * @param options the custom RoleOptions configuration
     * @return a new Selector instance for the role strategy
     */
    public static Selector byRole(AriaRole role, RoleOptions options) {
        return of(
                page -> page.getByRole(role, options.toPageOptions()),
                parent -> parent.getByRole(role, options.toLocatorOptions()));
    }

    /**
     * Locates an element by its associated label text.
     *
     * @param text the label text to search for
     * @return a new Selector instance for the label strategy
     */
    public static Selector byLabel(String text) {
        return of(page -> page.getByLabel(text), parent -> parent.getByLabel(text));
    }

    /**
     * Locates an element by its associated label text with exact matching configuration.
     *
     * @param text  the label text to search for
     * @param exact true to match the exact text, false for substring matching
     * @return a new Selector instance for the label strategy
     */
    public static Selector byLabel(String text, boolean exact) {
        return of(
                page -> page.getByLabel(text, new Page.GetByLabelOptions().setExact(exact)),
                parent -> parent.getByLabel(text, new Locator.GetByLabelOptions().setExact(exact)));
    }

    /**
     * Locates an element by its associated label text using a regular expression.
     *
     * @param pattern the regular expression pattern
     * @return a new Selector instance for the label strategy
     */
    public static Selector byLabel(Pattern pattern) {
        return of(page -> page.getByLabel(pattern), parent -> parent.getByLabel(pattern));
    }

    /**
     * Locates an element by its placeholder attribute.
     *
     * @param text the placeholder text to search for
     * @return a new Selector instance for the placeholder strategy
     */
    public static Selector byPlaceholder(String text) {
        return of(page -> page.getByPlaceholder(text), parent -> parent.getByPlaceholder(text));
    }

    /**
     * Locates an element by its placeholder attribute with exact matching configuration.
     *
     * @param text  the placeholder text to search for
     * @param exact true to match the exact text, false for substring matching
     * @return a new Selector instance for the placeholder strategy
     */
    public static Selector byPlaceholder(String text, boolean exact) {
        return of(
                page -> page.getByPlaceholder(text, new Page.GetByPlaceholderOptions().setExact(exact)),
                parent -> parent.getByPlaceholder(text, new Locator.GetByPlaceholderOptions().setExact(exact)));
    }

    /**
     * Locates an element by its placeholder attribute using a regular expression.
     *
     * @param pattern the regular expression pattern
     * @return a new Selector instance for the placeholder strategy
     */
    public static Selector byPlaceholder(Pattern pattern) {
        return of(page -> page.getByPlaceholder(pattern), parent -> parent.getByPlaceholder(pattern));
    }

    /**
     * Locates an element by its alt text attribute.
     *
     * @param text the alt text to search for
     * @return a new Selector instance for the alt text strategy
     */
    public static Selector byAltText(String text) {
        return of(page -> page.getByAltText(text), parent -> parent.getByAltText(text));
    }

    /**
     * Locates an element by its alt text attribute with exact matching configuration.
     *
     * @param text  the alt text to search for
     * @param exact true to match the exact text, false for substring matching
     * @return a new Selector instance for the alt text strategy
     */
    public static Selector byAltText(String text, boolean exact) {
        return of(
                page -> page.getByAltText(text, new Page.GetByAltTextOptions().setExact(exact)),
                parent -> parent.getByAltText(text, new Locator.GetByAltTextOptions().setExact(exact)));
    }

    /**
     * Locates an element by its alt text attribute using a regular expression.
     *
     * @param pattern the regular expression pattern
     * @return a new Selector instance for the alt text strategy
     */
    public static Selector byAltText(Pattern pattern) {
        return of(page -> page.getByAltText(pattern), parent -> parent.getByAltText(pattern));
    }

    /**
     * Locates an element by its title attribute.
     *
     * @param text the title text to search for
     * @return a new Selector instance for the title strategy
     */
    public static Selector byTitle(String text) {
        return of(page -> page.getByTitle(text), parent -> parent.getByTitle(text));
    }

    /**
     * Locates an element by its title attribute with exact matching configuration.
     *
     * @param text  the title text to search for
     * @param exact true to match the exact text, false for substring matching
     * @return a new Selector instance for the title strategy
     */
    public static Selector byTitle(String text, boolean exact) {
        return of(
                page -> page.getByTitle(text, new Page.GetByTitleOptions().setExact(exact)),
                parent -> parent.getByTitle(text, new Locator.GetByTitleOptions().setExact(exact)));
    }

    /**
     * Locates an element by its title attribute using a regular expression.
     *
     * @param pattern the regular expression pattern
     * @return a new Selector instance for the title strategy
     */
    public static Selector byTitle(Pattern pattern) {
        return of(page -> page.getByTitle(pattern), parent -> parent.getByTitle(pattern));
    }

    /**
     * Locates an element by its test-id attribute.
     *
     * @param testId the test-id string to search for
     * @return a new Selector instance for the test-id strategy
     */
    public static Selector byTestId(String testId) {
        return of(page -> page.getByTestId(testId), parent -> parent.getByTestId(testId));
    }

    /**
     * Locates an element by its test-id attribute using a regular expression.
     *
     * @param pattern the regular expression pattern
     * @return a new Selector instance for the test-id strategy
     */
    public static Selector byTestId(Pattern pattern) {
        return of(page -> page.getByTestId(pattern), parent -> parent.getByTestId(pattern));
    }

    /**
     * Chains a child selector to this parent selector dynamically.
     * Evaluates the parent selector first, then evaluates the child selector against the resolved parent.
     *
     * @param child the child Selector strategy to append
     * @return a new combined Selector instance
     */
    public Selector chain(Selector child) {
        if (child == null) {
            return this;
        }

        Selector parentSelector = this;
        return of(
                page -> child.evaluate(parentSelector.evaluate(page)),
                parent -> child.evaluate(parentSelector.evaluate(parent)));
    }

    /**
     * Narrows this selector to the single element at the given zero-based index, evaluated lazily.
     * <p>
     * Equivalent to appending Playwright's {@code nth(index)} to whatever this selector resolves to,
     * without eagerly resolving a {@link Locator} up front. Used to give each element inside a
     * collection or table row its own stable, individually addressable Selector.
     * </p>
     *
     * @param index the zero-based index of the element to target
     * @return a new Selector scoped to the element at the given index
     */
    public Selector nth(int index) {
        Selector self = this;
        return of(
                page -> self.evaluate(page).nth(index),
                parent -> self.evaluate(parent).nth(index));
    }

    /**
     * Builds a Selector from a pair of resolution functions, one for each possible evaluation root.
     * <p>
     * This is the single implementation point shared by every {@code byX(...)} factory below, so that
     * each strategy only needs to describe how to resolve itself from a {@link Page} and from a
     * {@link Locator}, without repeating the anonymous class boilerplate.
     * </p>
     *
     * @param onPage    resolves the locator when evaluated against a root {@link Page}
     * @param onLocator resolves the locator when evaluated against a parent {@link Locator}
     * @return a new Selector backed by the given resolution functions
     */
    private static Selector of(Function<Page, Locator> onPage, Function<Locator, Locator> onLocator) {
        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return onPage.apply(page);
            }

            @Override
            public Locator evaluate(Locator parent) {
                return onLocator.apply(parent);
            }
        };
    }
}