package org.jcd2052.core.browser.services;

import org.jcd2052.core.browser.configuration.IBrowserProperties;
import org.jcd2052.core.browser.services.interfaces.IElementFactory;
import org.jcd2052.core.browser.services.interfaces.IElementFinderService;
import org.jcd2052.core.elements.AbstractElement;
import org.jcd2052.core.elements.ButtonElement;
import org.jcd2052.core.elements.selector.Selector;
import org.jcd2052.core.elements.interfaces.IButtonElement;
import org.jcd2052.core.elements.interfaces.IElement;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

/**
 * Unit tests for {@link ElementFactory}, in particular the reflection-based instantiation path
 * ({@code getDefaultElementSupplier}) and the interface-to-implementation registry, without
 * needing a real Page or browser.
 */
public class ElementFactoryTest {
    @Test
    public void createCustomElementInstantiatesTheRegisteredBuiltInImplementation() {
        ElementFactory factory = newFactory();
        Selector selector = Selector.byTestId("save-button");

        IButtonElement element = factory.createCustomElement(IButtonElement.class, selector, "Save button");

        assertTrue(element instanceof ButtonElement);
        assertSame(element.getSelector(), selector);
        assertEquals(element.getName(), "Save button");
    }

    @Test
    public void createCustomElementThrowsForAnUnregisteredInterface() {
        ElementFactory factory = newFactory();

        IllegalArgumentException exception = expectThrows(IllegalArgumentException.class, () ->
                factory.createCustomElement(IUnregisteredElement.class, Selector.bySelector("#x"), "x"));

        assertTrue(exception.getMessage().contains("IUnregisteredElement"));
    }

    @Test
    public void registerElementAddsACustomInterfaceMapping() {
        ElementFactory factory = newFactory();
        factory.registerElement(ICustomTestElement.class, CustomTestElement.class);

        ICustomTestElement element = factory.createCustomElement(
                ICustomTestElement.class, Selector.bySelector("#custom"), "Custom element");

        assertTrue(element instanceof CustomTestElement);
    }

    @Test
    public void combineSelectorsChainsParentAndChild() {
        ElementFactory factory = newFactory();
        Selector parentSelector = Selector.bySelector("#parent");
        Selector childSelector = Selector.byText("Child");

        Selector combined = factory.combineSelectors(parentSelector, childSelector);

        assertEquals(combined.toString(), "bySelector(\"#parent\") -> byText(\"Child\")");
    }

    @Test
    public void combineSelectorsWithNullParentReturnsChildUnchanged() {
        ElementFactory factory = newFactory();
        Selector childSelector = Selector.byText("Child");

        assertSame(factory.combineSelectors((Selector) null, childSelector), childSelector);
    }

    private ElementFactory newFactory() {
        IElementFinderService finderService = mock(IElementFinderService.class);
        IBrowserProperties browserProperties = mock(IBrowserProperties.class);
        when(browserProperties.isHighlight()).thenReturn(false);
        return new ElementFactory(finderService, browserProperties);
    }

    /**
     * An element interface intentionally never registered with any {@link ElementFactory},
     * used to verify the "unregistered interface" failure path.
     */
    private interface IUnregisteredElement extends IElement {
    }

    /**
     * A minimal custom element used to verify {@link ElementFactory#registerElement}. Mirrors the
     * constructor shape every framework element must expose: {@code (Selector, String, IElementFactory)}.
     */
    private interface ICustomTestElement extends IElement {
    }

    private static class CustomTestElement extends AbstractElement implements ICustomTestElement {
        private CustomTestElement(Selector selector, String name, IElementFactory elementFactory) {
            super(selector, name, elementFactory);
        }
    }
}
