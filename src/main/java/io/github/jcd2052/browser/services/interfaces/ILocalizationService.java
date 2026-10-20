package io.github.jcd2052.browser.services.interfaces;

import io.github.jcd2052.logger.i18n.LogAction;

public interface ILocalizationService {
    String getMessage(LogAction action, Object... args);
}
