# playwright-java

A wrapper around Playwright for Java (`io.github.jcd2052:playwright-java`, Java 17). It wraps raw Playwright calls in a
thread-local browser that is exposed as a service (`IBrowserService`), which Page Objects and their elements use.
No IoC container is required: Spring is in test scope only, as one example of how to wire the framework.
See `README.md` for the settings list and wiring examples.

## Architecture

Interfaces are used everywhere they can be (`IBrowser`, `IBrowserWindow`, `IBrowserTab`, `IBrowserService`,
`IBrowserFactory`, `IElementFactory`, `IElement` and one per element type, `IForm`, ...), so every part can be
replaced and the framework can grow. Depend on the interface, and put new behavior behind a new or extended interface
with its implementation next to it.

## Creating pages and elements

1. Do not expose the Playwright locator outside the pages. A page offers business methods; tests don't get
   `Locator`s or `Selector`s from it.
2. Declare elements with the interface type (`ITextBoxElement`, `IButtonElement`, ...), never the concrete class
   (`TextBoxElement`). Create them through the element factory (`getElementFactory().createTextBoxElement(...)`).

## Browser layer

Structure: `PlaywrightBrowser` (one per thread) -> `BrowserWindow` (many, belong to that browser) -> `BrowserTab` (many,
belong to that window). Windows wrap a Playwright `BrowserContext`, tabs wrap a `Page`.

- `BrowserProperties` / `IBrowserProperties`: POJO (Lombok, chainable setters) with the settings used to create a
  browser. Launch settings (name, headless, args, test-id attribute) apply when the browser starts; window settings
  (viewport, timeouts, locale, timezone, geolocation, permissions, device emulation, storage state) apply to every new
  window.
- Factory, launchers and services: see the next section.
- `PlaywrightBrowser`: one running browser process. Opens, switches and closes its windows.
- `BrowserWindow`: tab operations, cookies (`CookieManager`), the context.
- `BrowserTab`: navigation and other page operations, mouse, keyboard, storage, alerts. `handleNextAlert` dismisses
  any further dialog, and if the handler throws it dismisses the dialog and rethrows the exception to the caller.

Design rules for this layer:

- 1 thread = 1 browser. No collections of browsers shared between threads. Each thread closes its own browser in
  teardown.
- Keep state minimal and derive it from Playwright (`browser.contexts()`, `context.pages()`). Each layer stores only
  its current child: the browser keeps the current window, a window keeps the current tab. Wrappers are equal when
  they wrap the same `BrowserContext` or `Page`.
- To return to a window or tab, keep it in a variable and switch back to it. Windows don't remember their last tab,
  and the window list has no defined order.
- `PlaywrightBrowser` wraps an already running browser and never sees the factory; `BrowserService` does the creation.
- `IBrowserService` doesn't expose the browser properties.
- For structural changes to this layer, state your interpretation and confirm before rewriting.

## Factory, launchers and services (`browser/factory`, `browser/launcher`, `browser/services`)

Wiring order, all plain constructors, created once and shared by every thread:
`BrowserProperties` -> `BrowserLauncherRegistry` -> `BrowserFactory` -> `BrowserService`, and
`LocalizationService` + `ElementFinderService` -> `ElementFactory`.

- `IBrowserFactory.createBrowser(Playwright)` is the only factory method. `BrowserFactory<T extends IBrowserProperties>`
  builds the launch options (headless, args, launch timeout), applies the custom test-id attribute to the driver, and
  launches through the launcher registered for the configured name. An unknown name throws
  `IllegalArgumentException`. The launch timeout reuses `getTimeout()`, the element timeout.
- A launcher is `IBrowserLauncher`: `getName()` plus `launch(Playwright, LaunchOptions)`. Built in: `chrome` and `edge`
  (Chromium with the `chrome` / `msedge` channel), `chromium`, `firefox`, `webkit`. `BrowserLauncherRegistry`
  registers them and matches names case-insensitively; `register(launcher)` adds a custom one (the same name replaces
  the old one), and `getLauncher(null)` returns `null`.
- `BrowserService<T>` (`IBrowserService<T>`) keeps each thread's browser in a `ThreadLocal`. `getBrowser()` returns it,
  or replaces it when it is missing or closed. `setBrowser(factory)` closes the old browser, starts a new `Playwright`,
  creates the browser, opens its first window, and closes the new driver again if any of that fails. `close()` logs a
  failure instead of throwing, so teardown can't fail. Tracing (`isTracingEnabled`, `startTracing`,
  `stopAndSaveTrace`) works on the current window's context; callers check `isTracingEnabled()` first, because
  start and stop don't. `stopAndSaveTrace` never launches a browser: with no open browser it logs a warning and
  returns an empty array. A second constructor takes a `Supplier<Playwright>` so unit tests can mock the driver.
- `ElementFactory` (`IElementFactory`) creates elements, collections and child elements. The interface has a default
  method per element type (`createButtonElement`, `createTextBoxElement`, ...) and `combineSelectors`, which chains a
  child selector under its parent's. `IElementSupplier` is the functional interface for creating custom element
  types.
- `ElementFinderService.findElement(selector)` evaluates the selector on the calling thread's current tab.
- `LocalizationService` loads a language through `LocalizationReader` (a custom directory first, then the classpath
  `localization/<lang>.json`) and formats messages by key.

## Elements

- `AbstractElement` is the base entrance. All common interaction lives there: clicks (normal, double, force, right,
  middle, at position, via JS), `press`, `hover`, `unfocus`, `scrollToElement`, drag and drop (`dragTo`, and
  `dragAndDropTo` with mouse moves, which always releases the mouse button), `getText`, `getAllTexts`, `getAttribute`, `isVisible`, `isEnabled`,
  `getScreenshot`, `waitForState` / `waitForLoading`, and child element creation. `JsActions` covers the JS-based
  helpers (click, scroll, setAttribute, pointer-events and viewport checks).
- Logic specific to one element type lives in its own subclass: `ButtonElement`, `LabelElement`, `LinkElement`,
  `TextBoxElement`, `DropdownElement`, `CheckBoxElement` and `RadioButtonElement` (both extend
  `AbstractCheckableElement`), `UploadBox`. Each has an `I...` interface in `elements/interfaces`.
- An element stores only a `Selector` and a name, never a Playwright `Locator`. `getLocator()` asks
  `ElementFinderService`, which evaluates the selector on the calling thread's current tab, so the locator is resolved
  just in time and can't go stale. `Selector` (`elements/selector`) wraps the Playwright strategies (`bySelector`,
  `byText`, `byRole`, `byLabel`, `byPlaceholder`, `byTestId`, ...) plus `byFrame`, `chain` and `nth`; its `toString()`
  describes the strategy and is used in error messages.
- Elements are created through `ElementFactory`, which maps interfaces to implementations; register custom ones with
  `registerElement(interface, impl)`. Element constructors are `(Selector, String, IElementFactory)`, found by
  reflection. `ElementCollection` is a lazy list: it re-finds its elements on each `getElements()` and respects
  `ExpectedCount`.
- Actions go through `executeAction` / `executeActionReturning` in `AbstractElement`: they call Playwright, log the
  result with the element name and arguments, and turn a `PlaywrightException` into a framework exception via
  `PlaywrightExceptionParser`. New element actions should use these two methods and pass a `LogAction`. A logging or
  translation failure is only logged as a warning: it never fails or hides an action that already happened.
  `DropdownElement.getSelectedOption()` returns the first selected option of a multi-select.
- Logging is localized. Each `LogAction` constant has a key, and `LocalizationService` formats the message from
  `localization/en.json` or `uk.json`, with the element name as the first argument. A new action needs its `LogAction`
  constant and a key in both JSON files, otherwise `getMessage` throws `LocalizationException`
  (`LogActionTranslationsTest` checks the translations).

## Exceptions

The `exceptions` package holds the framework's own exceptions. Playwright's original messages are very long (they
carry the full call log), which makes it hard to see what went wrong, so failures are parsed and re-thrown as short
custom exceptions.

- `PlaywrightExceptionParser.parse(e, elementName, selector, actionName)` keeps only the primary reason (the text
  before "Call log:", without the leading "Error:") and builds a compact message: the failed action, the element name,
  the reason and the selector description. The original `PlaywrightException` is kept as the cause.
- Only a Playwright `TimeoutError` becomes `ElementTimeoutException` (the message text is not checked). Anything else
  becomes `ElementActionException`, with a hint when the page, context or browser was closed ("Target closed",
  "has been closed") or the element was detached during the action.
- `LocalizationException` is for missing or invalid log-message translations.
- All of them extend `BasePlaywrightException` (unchecked), so catching it handles any framework failure.
- `AbstractElement` and `ElementCollection` do the parsing; new element code should go through
  `executeAction` / `executeActionReturning` rather than throwing raw `PlaywrightException`s.

## Pages

`AbstractForm` (`pages/`, interface `IForm`) is the base entrance for Page Objects. A page is created with a
`Selector`, a name and an `IElementFactory`:

- The selector locates a marker element of the page (kept internally as a label element, `formLabel`). It is how the
  page checks that the right page is open.
- The name identifies the page in logs and error messages.
- The element factory creates the page's elements. A page stores its own locator plus the elements used to interact
  with it, usually as fields created from `getElementFactory()`, for example
  `getElementFactory().createTextBoxElement(Selector.byTestId("username-input"), "Username")`.
- Base methods, all based on the page's own locator: `isVisible()`, `waitForLoading(timeout)`,
  `waitToBeVisible(timeout)`, `waitToBeInvisible(timeout)` (each also has a no-timeout default). The wait methods
  return a boolean instead of throwing. `waitToBeInvisible` waits until the root element is removed from the DOM
  (see `IForm`); a root that is only hidden but still attached does not count.
- Pages hold no browser state: the elements find the current thread's browser each time, so a page instance can be
  shared between parallel tests.
- `getAllElements()` lists every `IElement` field of the page (including superclass fields) by reflection, keyed by
  field name.

Tables are built on top of it: `AbstractTableGridForm<M, R>` (`pages/table/gridsection`) is a grid with a row
selector and returns its rows (`getRowsFromPage()`) or their models (`getModelsFromRows()`); `AbstractRow<T>`
(`pages/table/row`) is a form bound to a 1-based row position, with its cells as child elements, and maps the row to a
model `T`. Working examples live in `src/test/java/io/github/jcd2052/demo/pages` (`DemoLoginPage`,
`DemoProductsPage`, `DemoProductRow`, `DemoFramePage`, `DemoElementsPage`).

## Waiting (`waiting`)

`IConditionalWait` / `ConditionalWait` waits for any custom condition, for cases Playwright's own waits don't cover. It
is created with a default timeout and polling interval (`Duration`s) and is optional in the wiring.

- `waitFor(condition, ...)` polls a `BooleanSupplier` and returns `true`, or `false` on timeout.
- `waitForTrue(condition, ...)` does the same but throws `TimeoutException` on timeout (or when the thread is
  interrupted).
- Both have many overloads that fill in `null` for the optional parts: timeout, polling interval, a message for the
  timeout error, and exception types to ignore. A condition that throws one of those types counts as "not yet true";
  any other exception is rethrown. A `null` timeout or interval uses the defaults. A `null` condition throws
  `IllegalArgumentException`.

## Logging (`logger`)

- `ILogger` is the framework's logging API (`info`, `debug`, `warn`, `error`, `fatal`, `debugElementAction`).
  `Logger` implements it over SLF4J and formats messages with `String.format`; a bad format string is logged with
  "[Format Error]" instead of throwing. `LoggerProvider.getLogger()` returns the one shared instance, and the framework
  code logs through it.
- The library ships no logging backend: consumers add their own SLF4J implementation (the tests use Logback).
- `logger/i18n` holds the localized action messages (see Elements): `ILogAction` (a key, plus a readable action name
  used in error messages, which defaults to the key; `ILogAction.of(key)` makes one from a plain key, so projects can
  log their own messages), the `LogAction` enum with the framework's actions, and `LocalizationReader`, which loads the
  `localization/<lang>.json` files (bundled: `en`, `uk`; a custom directory takes priority). The language comes from
  `playwright.logs.language` in the bundled tests.

## Playwright Java client quirks (1.61)

- `Browser.isConnected()` stays `true` after `Playwright.close()`, so `PlaywrightBrowser` keeps its own closed flag.
  A lost connection only shows up as a failed call. Detecting it was removed on purpose: don't re-add
  `isConnectionLost` or a `start()` recovery.
- `Browser.contexts()` is unordered (backed by a `HashSet`); `BrowserContext.pages()` is in creation order.
- `Playwright.close()` alone stops the driver and every browser it launched.
- `TargetClosedError` is internal to Playwright (`com.microsoft.playwright.impl`): don't depend on it.

## Code style

- No static imports and no wildcard imports. Use qualified calls: `Assert.assertEquals(...)`, `Mockito.when(...)`.
- Javadoc on public and non-obvious members.
- Tests (TestNG): `@Test(description = "...")` in plain language, plus a javadoc saying what the test verifies.
  Setup/teardown methods and non-obvious constants get javadoc too. No `// ---- Section ----` divider comments.
- Some older test files still use static imports; convert them only when asked.

## Build and test

Maven + TestNG (the Surefire TestNG provider is pinned in `pom.xml`; don't remove it, JUnit 5 on the classpath
would otherwise make Surefire run zero tests). Run all tests with `mvn test`, or one class with
`mvn test -Dtest=BrowserWindowTest`. The browser tests launch a real headless Chromium.

`mvn` may not be on the PATH and the repo has no `mvnw`. IntelliJ's bundled Maven works offline:
`"/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3/bin/mvn" -o test -Dgpg.skip`. A full run takes a
few minutes (the suite had 176 tests when last run).

## Known limitations

- A `null` `timeout`, `pageLoadTimeout`, `width` or `height` in the properties fails with a bare
  `NullPointerException` (the values are unboxed without a check).
- If `PlaywrightBrowser.openNewWindow` fails after creating the browser context, that context stays open.
- `LocalizationReader` uses a custom locale file instead of the bundled one, with no per-key merge, so a custom file
  needs every key.

## Packages (`src/main/java/io/github/jcd2052`)

- `browser/browser`, `browser/configuration`: browser, window and tab wrappers, and the properties
- `browser/factory`, `browser/launcher`, `browser/services`: see Factory, launchers and services
- `elements/` typed element wrappers, collections, `JsActions`, `selector/` (see Elements)
- `pages/` Page Object base classes, table grids and rows (see Pages)
- `exceptions/` custom exceptions and the Playwright exception parser (see Exceptions)
- `waiting/` conditional wait (see Waiting)
- `logger/` logging abstraction and localized messages (see Logging)
