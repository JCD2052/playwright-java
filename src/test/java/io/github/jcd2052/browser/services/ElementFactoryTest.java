package io.github.jcd2052.browser.services;

import io.github.jcd2052.browser.configuration.IBrowserProperties;
import io.github.jcd2052.browser.services.interfaces.IElementFactory;
import io.github.jcd2052.browser.services.interfaces.IElementFinderService;
import io.github.jcd2052.browser.services.interfaces.ILocalizationService;
import io.github.jcd2052.elements.AbstractElement;
import io.github.jcd2052.elements.ButtonElement;
import io.github.jcd2052.elements.selector.Selector;
import io.github.jcd2052.elements.interfaces.IButtonElement;
import io.github.jcd2052.elements.interfaces.IElement;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link ElementFactory}, in particular the reflection-based instantiation path
 * ({@code getDefaultElementSupplier}) and the interface-to-implementation registry, without
 * needing a real Page or browser.
 */
public class ElementFactoryTest {
    /**
     * Verifies that asking for a built-in element interface creates its registered implementation, with the
     * given selector and name.
     */
    @Test(description = "createCustomElement instantiates the registered built-in implementation")
    public void createCustomElementInstantiatesTheRegisteredBuiltInImplementation() {
        ElementFactory factory = newFactory();
        Selector selector = Selector.byTestId("save-button");

        IButtonElement element = factory.createCustomElement(IButtonElement.class, selector, "Save button");

        Assert.assertTrue(element instanceof ButtonElement);
        Assert.assertSame(element.getSelector(), selector);
        Assert.assertEquals(element.getName(), "Save button");
    }

    /**
     * Verifies that asking for an element interface with no registered implementation fails with an
     * {@link IllegalArgumentException} that names the interface and points to {@code registerElement}.
     */
    @Test(description = "createCustomElement throws for an unregistered interface")
    public void createCustomElementThrowsForAnUnregisteredInterface() {
        ElementFactory factory = newFactory();

        IllegalArgumentException exception = Assert.expectThrows(IllegalArgumentException.class, () ->
                factory.createCustomElement(IUnregisteredElement.class, Selector.bySelector("#x"), "x"));

        Assert.assertTrue(exception.getMessage().contains("IUnregisteredElement"));
        Assert.assertTrue(exception.getMessage().contains("registerElement"));
    }

    /**
     * Verifies that {@link ElementFactory#registerElement} maps a custom interface to its implementation, so the
     * factory can create it afterwards.
     */
    @Test(description = "registerElement adds a custom interface mapping")
    public void registerElementAddsACustomInterfaceMapping() {
        ElementFactory factory = newFactory();
        factory.registerElement(ICustomTestElement.class, CustomTestElement.class);

        ICustomTestElement element = factory.createCustomElement(
                ICustomTestElement.class, Selector.bySelector("#custom"), "Custom element");

        Assert.assertTrue(element instanceof CustomTestElement);
    }

    /**
     * Verifies that combining selectors chains the child under the parent.
     */
    @Test(description = "combineSelectors chains parent and child")
    public void combineSelectorsChainsParentAndChild() {
        ElementFactory factory = newFactory();
        Selector parentSelector = Selector.bySelector("#parent");
        Selector childSelector = Selector.byText("Child");

        Selector combined = factory.combineSelectors(parentSelector, childSelector);

        Assert.assertEquals(combined.toString(), "bySelector(\"#parent\") -> byText(\"Child\")");
    }

    /**
     * Verifies that combining a child selector with no parent returns the child selector itself.
     */
    @Test(description = "combineSelectors with a null parent returns the child unchanged")
    public void combineSelectorsWithNullParentReturnsChildUnchanged() {
        ElementFactory factory = newFactory();
        Selector childSelector = Selector.byText("Child");

        Assert.assertSame(factory.combineSelectors((Selector) null, childSelector), childSelector);
    }

    /**
     * Creates a factory with mocked collaborators: elements are only instantiated in these tests, never
     * looked up on a page.
     */
    private ElementFactory newFactory() {
        IElementFinderService finderService = Mockito.mock(IElementFinderService.class);
        IBrowserProperties browserProperties = Mockito.mock(IBrowserProperties.class);
        ILocalizationService localizationService = Mockito.mock(ILocalizationService.class);
        Mockito.when(browserProperties.isHighlight()).thenReturn(false);
        return new ElementFactory(finderService, browserProperties, localizationService);
    }

    /**
     * An element interface intentionally never registered with any {@link ElementFactory},
     * used to verify the "unregistered interface" failure path.
     */
    private interface IUnregisteredElement extends IElement {
    }

    /**
     * A minimal custom element interface used to verify {@link ElementFactory#registerElement}.
     */
    private interface ICustomTestElement extends IElement {
    }

    /**
     * The implementation registered for {@link ICustomTestElement}. Mirrors the constructor shape every
     * framework element must expose for the factory's reflection: {@code (Selector, String, IElementFactory)}.
     */
    private static class CustomTestElement extends AbstractElement implements ICustomTestElement {
        private CustomTestElement(Selector selector, String name, IElementFactory elementFactory) {
            super(selector, name, elementFactory);
        }
    }
}
