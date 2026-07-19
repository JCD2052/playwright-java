package org.jcd2052.core.elements.selector;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.regex.Pattern;

@Data
@Accessors(chain = true)
public class RoleOptions {
    private String nameString;
    private Pattern namePattern;
    private Boolean exact;
    private Boolean checked;
    private Boolean disabled;
    private Boolean expanded;
    private Boolean includeHidden;
    private Integer level;
    private Boolean pressed;
    private Boolean selected;

    /**
     * Translates to Page options
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
     * Translates to Locator options
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