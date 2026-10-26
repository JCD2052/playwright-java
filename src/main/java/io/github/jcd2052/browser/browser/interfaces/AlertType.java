package io.github.jcd2052.browser.browser.interfaces;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The kind of native browser dialog an {@link IAlert} represents.
 */
@AllArgsConstructor
public enum AlertType {
    /**
     * A JavaScript {@code alert(...)}: a message with an "OK" button.
     */
    ALERT("alert"),
    /**
     * A JavaScript {@code confirm(...)}: a question with "OK" and "Cancel".
     */
    CONFIRM("confirm"),
    /**
     * A JavaScript {@code prompt(...)}: a question with a text field, "OK" and "Cancel".
     */
    PROMPT("prompt"),
    /**
     * The "Leave site?" dialog a page can show when it is being closed or navigated away from.
     */
    BEFORE_UNLOAD("beforeunload");
    /**
     * Every constant by its Playwright type name.
     */
    private static final Map<String, AlertType> BY_PLAYWRIGHT_TYPE = Arrays.stream(values())
            .collect(Collectors.toMap(type -> type.playwrightType, Function.identity()));

    /**
     * The type name Playwright reports for this kind of dialog.
     */
    private final String playwrightType;

    /**
     * Maps Playwright's dialog type name ({@code "alert"}, {@code "confirm"}, {@code "prompt"},
     * {@code "beforeunload"}) to the matching constant.
     *
     * @param playwrightType The type name reported by Playwright.
     * @return The matching dialog type.
     * @throws IllegalArgumentException if the name is not a known dialog type.
     */
    public static AlertType fromPlaywrightType(String playwrightType) {
        AlertType type = BY_PLAYWRIGHT_TYPE.get(playwrightType);
        if (type == null) {
            throw new IllegalArgumentException("Unknown dialog type: " + playwrightType);
        }
        return type;
    }
}
