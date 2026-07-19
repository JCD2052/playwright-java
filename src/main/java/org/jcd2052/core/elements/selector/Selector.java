package org.jcd2052.core.elements.selector;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

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
        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return page.locator(selector);
            }

            @Override
            public Locator evaluate(Locator parent) {
                return parent.locator(selector);
            }
        };
    }

    /**
     * Locates an element by its text content.
     *
     * @param text the text to search for
     * @return a new Selector instance for the text strategy
     */
    public static Selector byText(String text) {
        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return page.getByText(text);
            }

            @Override
            public Locator evaluate(Locator parent) {
                return parent.getByText(text);
            }
        };
    }

    /**
     * Locates an element by its text content with exact matching configuration.
     *
     * @param text  the text to search for
     * @param exact true to match the exact text, false for substring matching
     * @return a new Selector instance for the text strategy
     */
    public static Selector byText(String text, boolean exact) {
        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return page.getByText(text, new Page.GetByTextOptions().setExact(exact));
            }

            @Override
            public Locator evaluate(Locator parent) {
                return parent.getByText(text, new Locator.GetByTextOptions().setExact(exact));
            }
        };
    }

    /**
     * Locates an element by its text content using a regular expression.
     *
     * @param pattern the regular expression pattern
     * @return a new Selector instance for the text strategy
     */
    public static Selector byText(Pattern pattern) {
        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return page.getByText(pattern);
            }

            @Override
            public Locator evaluate(Locator parent) {
                return parent.getByText(pattern);
            }
        };
    }

    /**
     * Locates an element by its ARIA role.
     *
     * @param role the ARIA role to search for
     * @return a new Selector instance for the role strategy
     */
    public static Selector byRole(AriaRole role) {
        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return page.getByRole(role);
            }

            @Override
            public Locator evaluate(Locator parent) {
                return parent.getByRole(role);
            }
        };
    }

    /**
     * Locates an element by its ARIA role and accessible name.
     *
     * @param role the ARIA role to search for
     * @param name the accessible name of the element
     * @return a new Selector instance for the role strategy
     */
    public static Selector byRole(AriaRole role, String name) {
        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return page.getByRole(role, new Page.GetByRoleOptions().setName(name));
            }

            @Override
            public Locator evaluate(Locator parent) {
                return parent.getByRole(role, new Locator.GetByRoleOptions().setName(name));
            }
        };
    }

    /**
     * Locates an element by its ARIA role with advanced configuration options.
     *
     * @param role    the ARIA role to search for
     * @param options the custom RoleOptions configuration
     * @return a new Selector instance for the role strategy
     */
    public static Selector byRole(AriaRole role, RoleOptions options) {
        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return page.getByRole(role, options.toPageOptions());
            }

            @Override
            public Locator evaluate(Locator parent) {
                return parent.getByRole(role, options.toLocatorOptions());
            }
        };
    }

    /**
     * Locates an element by its associated label text.
     *
     * @param text the label text to search for
     * @return a new Selector instance for the label strategy
     */
    public static Selector byLabel(String text) {
        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return page.getByLabel(text);
            }

            @Override
            public Locator evaluate(Locator parent) {
                return parent.getByLabel(text);
            }
        };
    }

    /**
     * Locates an element by its associated label text with exact matching configuration.
     *
     * @param text  the label text to search for
     * @param exact true to match the exact text, false for substring matching
     * @return a new Selector instance for the label strategy
     */
    public static Selector byLabel(String text, boolean exact) {
        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return page.getByLabel(text, new Page.GetByLabelOptions().setExact(exact));
            }

            @Override
            public Locator evaluate(Locator parent) {
                return parent.getByLabel(text, new Locator.GetByLabelOptions().setExact(exact));
            }
        };
    }

    /**
     * Locates an element by its associated label text using a regular expression.
     *
     * @param pattern the regular expression pattern
     * @return a new Selector instance for the label strategy
     */
    public static Selector byLabel(Pattern pattern) {
        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return page.getByLabel(pattern);
            }

            @Override
            public Locator evaluate(Locator parent) {
                return parent.getByLabel(pattern);
            }
        };
    }

    /**
     * Locates an element by its placeholder attribute.
     *
     * @param text the placeholder text to search for
     * @return a new Selector instance for the placeholder strategy
     */
    public static Selector byPlaceholder(String text) {
        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return page.getByPlaceholder(text);
            }

            @Override
            public Locator evaluate(Locator parent) {
                return parent.getByPlaceholder(text);
            }
        };
    }

    /**
     * Locates an element by its placeholder attribute with exact matching configuration.
     *
     * @param text  the placeholder text to search for
     * @param exact true to match the exact text, false for substring matching
     * @return a new Selector instance for the placeholder strategy
     */
    public static Selector byPlaceholder(String text, boolean exact) {
        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return page.getByPlaceholder(text, new Page.GetByPlaceholderOptions().setExact(exact));
            }

            @Override
            public Locator evaluate(Locator parent) {
                return parent.getByPlaceholder(text, new Locator.GetByPlaceholderOptions().setExact(exact));
            }
        };
    }

    /**
     * Locates an element by its placeholder attribute using a regular expression.
     *
     * @param pattern the regular expression pattern
     * @return a new Selector instance for the placeholder strategy
     */
    public static Selector byPlaceholder(Pattern pattern) {
        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return page.getByPlaceholder(pattern);
            }

            @Override
            public Locator evaluate(Locator parent) {
                return parent.getByPlaceholder(pattern);
            }
        };
    }

    /**
     * Locates an element by its alt text attribute.
     *
     * @param text the alt text to search for
     * @return a new Selector instance for the alt text strategy
     */
    public static Selector byAltText(String text) {
        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return page.getByAltText(text);
            }

            @Override
            public Locator evaluate(Locator parent) {
                return parent.getByAltText(text);
            }
        };
    }

    /**
     * Locates an element by its alt text attribute with exact matching configuration.
     *
     * @param text  the alt text to search for
     * @param exact true to match the exact text, false for substring matching
     * @return a new Selector instance for the alt text strategy
     */
    public static Selector byAltText(String text, boolean exact) {
        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return page.getByAltText(text, new Page.GetByAltTextOptions().setExact(exact));
            }

            @Override
            public Locator evaluate(Locator parent) {
                return parent.getByAltText(text, new Locator.GetByAltTextOptions().setExact(exact));
            }
        };
    }

    /**
     * Locates an element by its alt text attribute using a regular expression.
     *
     * @param pattern the regular expression pattern
     * @return a new Selector instance for the alt text strategy
     */
    public static Selector byAltText(Pattern pattern) {
        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return page.getByAltText(pattern);
            }

            @Override
            public Locator evaluate(Locator parent) {
                return parent.getByAltText(pattern);
            }
        };
    }

    /**
     * Locates an element by its title attribute.
     *
     * @param text the title text to search for
     * @return a new Selector instance for the title strategy
     */
    public static Selector byTitle(String text) {
        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return page.getByTitle(text);
            }

            @Override
            public Locator evaluate(Locator parent) {
                return parent.getByTitle(text);
            }
        };
    }

    /**
     * Locates an element by its title attribute with exact matching configuration.
     *
     * @param text  the title text to search for
     * @param exact true to match the exact text, false for substring matching
     * @return a new Selector instance for the title strategy
     */
    public static Selector byTitle(String text, boolean exact) {
        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return page.getByTitle(text, new Page.GetByTitleOptions().setExact(exact));
            }

            @Override
            public Locator evaluate(Locator parent) {
                return parent.getByTitle(text, new Locator.GetByTitleOptions().setExact(exact));
            }
        };
    }

    /**
     * Locates an element by its title attribute using a regular expression.
     *
     * @param pattern the regular expression pattern
     * @return a new Selector instance for the title strategy
     */
    public static Selector byTitle(Pattern pattern) {
        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return page.getByTitle(pattern);
            }

            @Override
            public Locator evaluate(Locator parent) {
                return parent.getByTitle(pattern);
            }
        };
    }

    /**
     * Locates an element by its test-id attribute.
     *
     * @param testId the test-id string to search for
     * @return a new Selector instance for the test-id strategy
     */
    public static Selector byTestId(String testId) {
        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return page.getByTestId(testId);
            }

            @Override
            public Locator evaluate(Locator parent) {
                return parent.getByTestId(testId);
            }
        };
    }

    /**
     * Locates an element by its test-id attribute using a regular expression.
     *
     * @param pattern the regular expression pattern
     * @return a new Selector instance for the test-id strategy
     */
    public static Selector byTestId(Pattern pattern) {
        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return page.getByTestId(pattern);
            }

            @Override
            public Locator evaluate(Locator parent) {
                return parent.getByTestId(pattern);
            }
        };
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
        return new Selector() {
            @Override
            public Locator evaluate(Page page) {
                return child.evaluate(parentSelector.evaluate(page));
            }

            @Override
            public Locator evaluate(Locator parent) {
                return child.evaluate(parentSelector.evaluate(parent));
            }
        };
    }
}