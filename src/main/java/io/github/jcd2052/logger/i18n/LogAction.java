package io.github.jcd2052.logger.i18n;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LogAction implements ILogAction {
    ELEMENT_CLICKED("element_clicked"),
    ELEMENT_DOUBLE_CLICKED("element_double_clicked"),
    ELEMENT_FORCE_CLICKED("element_force_clicked"),
    ELEMENT_RIGHT_CLICKED("element_right_clicked"),
    ELEMENT_MIDDLE_CLICKED("element_middle_clicked"),
    ELEMENT_CLICKED_AT("element_clicked_at"),
    KEY_PRESSED("key_pressed"),
    ELEMENT_DRAGGED_TO("element_dragged_to"),
    TEXTS_READ("texts_read"),
    TEXT_READ("text_read"),
    ELEMENT_VISIBILITY_CHECKED("element_visibility_checked"),
    ELEMENT_ENABLED_CHECKED("element_enabled_checked"),
    ATTRIBUTE_READ("attribute_read"),
    ELEMENT_HIGHLIGHTED("element_highlighted"),
    ACTION_FAILED("action_failed"),
    ELEMENT_CHECKED("element_checked"),
    ELEMENT_UNCHECKED("element_unchecked"),
    ELEMENT_IS_CHECKED("element_is_checked"),
    DROPDOWN_OPTION_READ("dropdown_option_read"),
    DROPDOWN_OPTIONS_READ("dropdown_options_read"),
    OPTION_SELECTED_BY_VALUE("option_selected_by_value"),
    OPTION_SELECTED_BY_INDEX("option_selected_by_index"),
    OPTION_SELECTED_BY_TEXT("option_selected_by_text"),
    INPUT_VALUE_READ("input_value_read"),
    TEXT_ENTERED("text_entered"),
    TEXT_CLEARED("text_cleared"),
    TEXT_CLEARED_AND_FILLED("text_cleared_and_filled"),
    FILE_UPLOADED("file_uploaded"),
    LINK_HREF_READ("link_href_read");

    private final String key;
}