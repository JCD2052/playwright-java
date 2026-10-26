package io.github.jcd2052.logger.i18n;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The element actions the framework logs, each mapped to a message key in the localization files
 * ({@code localization/<language>.json}).
 * <p>
 * Every message is a {@link String#format(String, Object...)} template whose first argument is the element's
 * name; some take further arguments, noted on the constant. Every constant must have a message in each bundled
 * locale, or the action that logs it fails. Each constant also has a short action name (e.g. "click"), used in
 * error messages.
 * </p>
 */
@AllArgsConstructor
@Getter
public enum LogAction implements ILogAction {
    /** An element was clicked. */
    ELEMENT_CLICKED("element_clicked", "click"),
    /** An element was double-clicked. */
    ELEMENT_DOUBLE_CLICKED("element_double_clicked", "double-click"),
    /** An element was clicked with Playwright's actionability checks skipped. */
    ELEMENT_FORCE_CLICKED("element_force_clicked", "force-click"),
    /** An element was right-clicked. */
    ELEMENT_RIGHT_CLICKED("element_right_clicked", "right-click"),
    /** An element was clicked with the middle mouse button. */
    ELEMENT_MIDDLE_CLICKED("element_middle_clicked", "middle-click"),
    /** An element was clicked at an offset; further arguments: the x and y offsets. */
    ELEMENT_CLICKED_AT("element_clicked_at", "click at position"),
    /** A key was pressed on an element; further argument: the key. */
    KEY_PRESSED("key_pressed", "press key"),
    /** An element was dragged onto another; further argument: the target element's name. */
    ELEMENT_DRAGGED_TO("element_dragged_to", "drag and drop"),
    /** The mouse was moved over an element. */
    ELEMENT_HOVERED("element_hovered", "hover"),
    /** The focus was moved away from an element. */
    ELEMENT_UNFOCUSED("element_unfocused", "unfocus"),
    /** An element was scrolled into view. */
    ELEMENT_SCROLLED_TO("element_scrolled_to", "scroll into view"),
    /** An element was clicked through JavaScript. */
    ELEMENT_JS_CLICKED("element_js_clicked", "click with JavaScript"),
    /** A screenshot of an element was taken. */
    ELEMENT_SCREENSHOT_TAKEN("element_screenshot_taken", "take screenshot"),
    /** An element reached the state it was waited for; further argument: the state. */
    ELEMENT_STATE_REACHED("element_state_reached", "wait for state"),
    /** An element did not reach the state it was waited for in time; further argument: the state. */
    ELEMENT_STATE_NOT_REACHED("element_state_not_reached", "wait for state"),
    /** The texts of every element matching a selector were read. */
    TEXTS_READ("texts_read", "read all texts"),
    /** An element's text was read. */
    TEXT_READ("text_read", "read text"),
    /** An element's visibility was checked. */
    ELEMENT_VISIBILITY_CHECKED("element_visibility_checked", "check visibility"),
    /** Whether an element is enabled was checked. */
    ELEMENT_ENABLED_CHECKED("element_enabled_checked", "check if enabled"),
    /** An element's attribute was read; further argument: the attribute name. */
    ATTRIBUTE_READ("attribute_read", "read attribute"),
    /** An element was highlighted for debugging. */
    ELEMENT_HIGHLIGHTED("element_highlighted", "highlight"),
    /** An action on an element failed (logged at ERROR level); further argument: the reason. */
    ACTION_FAILED("action_failed", "act"),
    /** A checkbox was checked. */
    ELEMENT_CHECKED("element_checked", "check"),
    /** A checkbox was unchecked. */
    ELEMENT_UNCHECKED("element_unchecked", "uncheck"),
    /** Whether a checkbox or radio button is checked was read. */
    ELEMENT_IS_CHECKED("element_is_checked", "check if checked"),
    /** A dropdown's selected option was read. */
    DROPDOWN_OPTION_READ("dropdown_option_read", "read selected option"),
    /** All of a dropdown's options were read. */
    DROPDOWN_OPTIONS_READ("dropdown_options_read", "read options"),
    /** A dropdown option was selected by its value; further argument: the value. */
    OPTION_SELECTED_BY_VALUE("option_selected_by_value", "select option by value"),
    /** A dropdown option was selected by its index; further argument: the index. */
    OPTION_SELECTED_BY_INDEX("option_selected_by_index", "select option by index"),
    /** A dropdown option was selected by its text; further argument: the text. */
    OPTION_SELECTED_BY_TEXT("option_selected_by_text", "select option by text"),
    /** A text box's value was read. */
    INPUT_VALUE_READ("input_value_read", "read input value"),
    /** Text was entered into a text box; further argument: the text. */
    TEXT_ENTERED("text_entered", "fill text"),
    /** A text box was cleared. */
    TEXT_CLEARED("text_cleared", "clear text"),
    /** A text box's value was replaced; further argument: the new text. */
    TEXT_CLEARED_AND_FILLED("text_cleared_and_filled", "clear and fill text"),
    /** A file was uploaded through an upload box; further argument: the file name. */
    FILE_UPLOADED("file_uploaded", "upload file"),
    /** A link's {@code href} was read. */
    LINK_HREF_READ("link_href_read", "read href");

    /**
     * The message key in the localization files.
     */
    private final String key;
    /**
     * The short, readable name of the action, shown in error messages.
     */
    private final String actionName;
}