package io.github.jcd2052.elements.selector;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.regex.Pattern;

/**
 * Configuration options for {@link Selector#byRole(com.microsoft.playwright.options.AriaRole, RoleOptions)},
 * mirroring Playwright's {@link Page.GetByRoleOptions}/{@link Locator.GetByRoleOptions}. Only the
 * fields that are set are applied; everything else falls back to Playwright's own defaults.
 */
@Data
@Accessors(chain = true)
public class RoleOptions {
    /**
     * The accessible name to match as text; ignored if {@link #namePattern} is set.
     */
    private String nameString;
    /**
     * The accessible name to match as a regular expression; takes precedence over {@link #nameString}.
     */
    private Pattern namePattern;
    /**
     * Whether the name must match exactly (case-sensitive, whole string) instead of as a substring.
     */
    private Boolean exact;
    /**
     * Match only elements that are (or are not) checked, e.g. checkboxes.
     */
    private Boolean checked;
    /**
     * Match only elements that are (or are not) disabled.
     */
    private Boolean disabled;
    /**
     * Match only elements that are (or are not) expanded, e.g. menus or tree items.
     */
    private Boolean expanded;
    /**
     * Whether to also match elements hidden from the accessibility tree.
     */
    private Boolean includeHidden;
    /**
     * Match only headings (and similar roles) of this level, e.g. 1 for {@code <h1>}.
     */
    private Integer level;
    /**
     * Match only elements that are (or are not) pressed, e.g. toggle buttons.
     */
    private Boolean pressed;
    /**
     * Match only elements that are (or are not) selected, e.g. tabs or options.
     */
    private Boolean selected;

    /**
     * Converts the set options into Playwright's options for {@code Page.getByRole}.
     *
     * @return The page-level role options.
     */
    public Page.GetByRoleOptions toPageOptions() {
        Page.GetByRoleOptions options = new Page.GetByRoleOptions();
        if (nameString != null) options.setName(nameString);
        if (namePattern != null) options.setName(namePattern);
        if (exact != null) options.setExact(exact);
        if (checked != null) options.setChecked(checked);
        if (disabled != null) options.setDisabled(disabled);
        if (expanded != null) options.setExpanded(expanded);
        if (includeHidden != null) options.setIncludeHidden(includeHidden);
        if (level != null) options.setLevel(level);
        if (pressed != null) options.setPressed(pressed);
        if (selected != null) options.setSelected(selected);
        return options;
    }

    /**
     * Converts the set options into Playwright's options for {@code Locator.getByRole}.
     *
     * @return The locator-level role options.
     */
    public Locator.GetByRoleOptions toLocatorOptions() {
        Locator.GetByRoleOptions options = new Locator.GetByRoleOptions();

        if (nameString != null) options.setName(nameString);
        if (namePattern != null) options.setName(namePattern);
        if (exact != null) options.setExact(exact);
        if (checked != null) options.setChecked(checked);
        if (disabled != null) options.setDisabled(disabled);
        if (expanded != null) options.setExpanded(expanded);
        if (includeHidden != null) options.setIncludeHidden(includeHidden);
        if (level != null) options.setLevel(level);
        if (pressed != null) options.setPressed(pressed);
        if (selected != null) options.setSelected(selected);
        return options;
    }
}