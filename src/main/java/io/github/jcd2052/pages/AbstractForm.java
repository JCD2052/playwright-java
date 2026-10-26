package io.github.jcd2052.pages;

import lombok.AccessLevel;
import lombok.Getter;
import io.github.jcd2052.browser.services.interfaces.IElementFactory;
import io.github.jcd2052.elements.selector.Selector;
import io.github.jcd2052.elements.interfaces.IElement;
import io.github.jcd2052.elements.interfaces.ILabelElement;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * The base class of page objects: a page, form or page fragment with a root element (the {@code formLabel})
 * that identifies it.
 * <p>
 * Subclasses create their elements with {@code getElementFactory()}, usually as fields. Elements resolve their
 * selectors on the current tab when they act, so one page-object instance (e.g. a Spring singleton) can be used
 * by every test and thread. The form's visibility and waits all refer to its root element.
 * </p>
 */
@Getter
public abstract class AbstractForm implements IForm {
    /**
     * The form's name, used in log messages and errors.
     */
    private final String name;
    /**
     * The element that identifies the form, usually its container; the form's waits refer to it.
     */
    @Getter(value = AccessLevel.PROTECTED)
    private final ILabelElement formLabel;
    /**
     * The factory the form's elements are created with.
     */
    @Getter(value = AccessLevel.PROTECTED)
    private final IElementFactory elementFactory;

    /**
     * Creates a form.
     *
     * @param locatorStrategy The selector of the form's root element, which should be unique to this form.
     * @param name            The form's name, e.g. "Login form".
     * @param elementFactory  The factory the form's elements are created with.
     */
    protected AbstractForm(Selector locatorStrategy, String name, IElementFactory elementFactory) {
        this.name = name;
        this.elementFactory = elementFactory;
        this.formLabel = elementFactory.createLabelElement(locatorStrategy, name);
    }

    @Override
    public boolean isVisible() {
        return getFormLabel().isVisible();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean waitForLoading(Double timeout) {
        return getFormLabel().waitForLoading(null, timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean waitToBeVisible(Double timeout) {
        return getFormLabel().waitToBeVisible(timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean waitToBeInvisible(Double timeout) {
        return getFormLabel().waitToBeDetached(timeout);
    }

    /**
     * Reflectively collects every {@link IElement}-typed field declared on this form (including
     * inherited fields from superclasses), keyed by field name.
     * <p>
     * Intended for framework-level tooling (e.g. reporting, debugging) rather than everyday page
     * object code.
     *
     * @return a map of field name to the corresponding {@link IElement} instance
     */
    protected Map<String, IElement> getAllElements() {
        Map<String, IElement> elementsMap = new HashMap<>();
        Class<?> currentClass = this.getClass();
        while (currentClass != null && currentClass != Object.class) {

            for (Field field : currentClass.getDeclaredFields()) {
                if (IElement.class.isAssignableFrom(field.getType())) {
                    try {
                        field.setAccessible(true);
                        IElement element = (IElement) field.get(this);

                        if (element != null) {
                            elementsMap.put(field.getName(), element);
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(
                                "Failed to read element field via reflection: " + field.getName(), e);
                    } finally {
                        field.setAccessible(false);
                    }
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        return elementsMap;
    }
}