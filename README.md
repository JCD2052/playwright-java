# Playwright Java Automation Framework

A lightweight UI automation framework for Java web tests built on top of Playwright.
The framework is organized around Page Objects, typed element wrappers, a browser → window → tab model,
and TestNG execution. It doesn't depend on any dependency injection container: wire it by hand, with
Spring, with Guice, or with any other IoC framework.

## Tools Used

* Java 17
* Playwright for Java
* Spring Boot, in test scope only, to show in the bundled tests how the framework can be wired with
  dependency injection and property-based configuration. The library itself doesn't depend on Spring or
  on any other IoC container.
* TestNG for test execution and data-driven parallel runs
* Maven for dependency and build management
* Lombok for simple model classes (compile-time only)
* SLF4J for framework logging. The library ships no logging backend or configuration, so add your
  own (e.g. Logback); the bundled tests use Logback through Spring Boot.

## Installation

```xml
<dependency>
    <groupId>io.github.jcd2052</groupId>
    <artifactId>playwright-java</artifactId>
    <version>2.0.0</version>
</dependency>
```

### Upgrading from 1.x

2.0.0 reworks the browser layer around one browser per thread, so some 1.x code no longer compiles:

* `IBrowserService`:
  * `start()` is gone: `getBrowser()` launches the browser with its first window when the thread has none.
  * `closeAll()` is gone: each thread calls `close()` in its own teardown.
  * `navigateTo(url)` moved to `IBrowser`: `browserService.getBrowser().navigateTo(url)`.
  * `getBrowserProperties()` is gone, here and on `IBrowserFactory`; keep the properties where you create them.
  * `startTracing()` and `stopAndSaveTrace(...)` no longer check the tracing setting: call them only when
    `isTracingEnabled()` is `true`.
* `IBrowserFactory` is no longer generic: `IBrowserFactory<IBrowserProperties>` becomes `IBrowserFactory`.
* `IBrowserProperties.isScreenshots()`/`isSnapshots()` are now `isTracingScreenshotsEnabled()`/
  `isTracingSnapshotsEnabled()`, with the setters `setTracingScreenshotsEnabled(...)`/`setTracingSnapshotsEnabled(...)`.
* `IBrowser`:
  * `reload()` is gone: use `requireCurrentBrowserTab().reloadTab()`.
  * `getBrowserName()` is gone: the name is in your properties.
  * `setCurrentBrowserWindow(...)` is gone: use `switchToWindow(...)`.
* `IBrowserWindow.setCurrentBrowserTab(...)` is gone: use `switchToTab(...)`.
* `getBrowserWindows()` and `getBrowserTabs()` return a `List` instead of a `Collection`.
* `IBrowserTab.handleNextAlert(...)` returns the handled `IAlert` instead of nothing.
* `ILocalizationService.getMessage(...)` takes an `ILogAction` instead of a `LogAction`, so your own log
  messages can be used too.

Code that implements the framework's interfaces itself must also add the new methods:
* `IAlert.getType()` and `getDefaultValue()`
* `IBrowserTab.getKeyboardActions()`
* `IBrowserWindow.closeTab(...)`
* `IBrowser.closeWindow(...)`
* `IJsActions.isInViewport()`

## How It Works

The framework keeps Page Objects and Elements small. Page classes describe the screen, elements wrap
Playwright locators, and tests call page-level business methods.

Elements store a `Selector` strategy and resolve the live Playwright `Locator` at the moment an action is
executed. This Just-In-Time lookup avoids stale element errors and keeps tests resilient on single-page
applications, where the DOM changes all the time.

The browser is modelled like a real one: an `IBrowser` is one browser process with its own windows
(Playwright browser contexts), and each window has its own tabs (pages). Windows never share cookies or
storage, and a browser only reaches its own windows and tabs.

Browsers are managed by `BrowserService`, on the rule **one thread, one browser**. `getBrowser()` returns
the calling thread's running browser, or launches a new one with a first window when the thread has none;
`close()` closes it in teardown. Threads never share a browser, so TestNG runs such as
`@DataProvider(parallel = true)` get isolated sessions without sharing cookies, cache, storage, or pages.

## Project Structure

```text
src/main/java/io/github/jcd2052
  browser/
    browser/       Browser, window (context) and tab (page) wrappers, cookies, storage, alerts, mouse, keyboard
    configuration/ IBrowserProperties and its BrowserProperties implementation
    factory/       BrowserFactory: turns properties into a launched Playwright browser
    launcher/      Per-browser launchers (chrome, chromium, edge, firefox, webkit) and their registry
    services/      BrowserService (per-thread browser lifecycle), element factory/finder, localization
  elements/        Typed element wrappers, collections, JS actions, and the Selector strategies
  pages/           Base form/page abstractions, table grids, and position-bound rows
  waiting/         Generic conditional wait utility
  exceptions/      Framework exceptions and the Playwright exception parser
  logger/          Logging abstraction and localized log messages (resources: localization/*.json)

src/test/java/io/github/jcd2052
  configuration/   Spring test configuration wiring all framework beans from properties
  demo/pages/      Example Page Objects (login form, products table, iframe widget, element showcase)
  demo/support/    DemoServer — bundled static file server for the demo pages
  demo/tests/      SpringBootBaseTests (browser lifecycle), DemoSiteBaseTests (demo server) and suites
  browser/         Real-browser tests of the browser, window, tab, alert and cookie wrappers
  ...              Unit tests for the other framework packages
```

## Browser Settings

Browser settings are the fields of `BrowserProperties`, set in code or loaded from any configuration
source. The bundled tests read them from Spring properties; the example values live in
`src/test/resources/application.properties`.

```properties
playwright.browser.headless=true
playwright.browser.name=chrome
playwright.browser.timeout=5000
playwright.browser.page.load.timeout=30000
playwright.browser.viewport.width=1600
playwright.browser.viewport.height=900
playwright.browser.tracing=false
playwright.browser.tracing.screenshots=true
playwright.browser.tracing.snapshots=true
playwright.browser.tracing.folder=target/tracing
playwright.browser.tracing.args=--no-sandbox,--disable-dev-shm-usage,--disable-gpu
playwright.browser.highlight=false
playwright.browser.testid.attribute=data-a-test
playwright.browser.locale=en-US
playwright.browser.timezone=Europe/Kyiv
playwright.browser.geolocation.latitude=50.4501
playwright.browser.geolocation.longitude=30.5234
playwright.browser.permissions=geolocation,notifications
playwright.browser.user-agent=Mozilla/5.0 (custom agent)
playwright.browser.device.scale.factor=2.0
playwright.browser.mobile=false
playwright.browser.has.touch=false
playwright.browser.storage.state.path=target/storage-state.json
playwright.logs.language=en
```

Supported browser names:

* `chrome` — installed Google Chrome
* `chromium` — Playwright's bundled Chromium (no Chrome install needed; a good fit for CI)
* `firefox`
* `edge` — installed Microsoft Edge
* `webkit`

Settings:

* `playwright.browser.headless` controls visible vs headless browser execution.
* `playwright.browser.name` selects the browser launcher.
* `playwright.browser.timeout` sets the default Playwright action timeout of every window. It is
  also used as the browser launch timeout, so keep it above a cold browser start (a few seconds).
* `playwright.browser.page.load.timeout` sets the default navigation timeout.
* `playwright.browser.viewport.width` and `playwright.browser.viewport.height` set the viewport size.
* `playwright.browser.tracing` enables Playwright tracing; `playwright.browser.tracing.screenshots` and
  `playwright.browser.tracing.snapshots` control trace detail, and `playwright.browser.tracing.folder` controls
  where trace ZIP files are written.
* `playwright.browser.tracing.args` passes comma-separated launch arguments to the browser.
* `playwright.browser.highlight` highlights elements before supported actions.
* `playwright.browser.testid.attribute` overrides the default `data-testid` attribute globally for `Selector.byTestId()`.
* `playwright.browser.locale` and `playwright.browser.timezone` emulate a locale (e.g. `en-US`) and a
  time zone (e.g. `Europe/Kyiv`).
* `playwright.browser.geolocation.latitude` and `playwright.browser.geolocation.longitude` emulate a
  location; both are needed, plus `geolocation` in the permissions.
* `playwright.browser.permissions` grants comma-separated browser permissions to every window, e.g.
  `geolocation`, `notifications`, `clipboard-read`, `clipboard-write`.
* `playwright.browser.user-agent`, `playwright.browser.device.scale.factor`, `playwright.browser.mobile`
  and `playwright.browser.has.touch` emulate a device.
* `playwright.browser.storage.state.path` preloads a saved storage state (cookies, local storage) into
  every new window. The file must already exist; leave the property unset otherwise.
* `playwright.logs.language` selects the language of element action logs (bundled: `en`, `uk`).

## Wiring the Framework

The framework is a handful of plain objects with constructor dependencies, created once and shared by all
tests and threads:

* `BrowserProperties` — the settings
* `BrowserLauncherRegistry` → `BrowserFactory` → `BrowserService` — launching and keeping one browser per thread
* `LocalizationService` and `ElementFinderService` → `ElementFactory` — creating elements
* `ConditionalWait` — waiting for custom conditions (optional)

Pages and elements hold no browser state: every action finds the calling thread's browser through
`BrowserService`, so one page instance can be shared by parallel tests. Spring is not required; the
bundled tests use it only as an example of dependency injection, and any IoC framework, or none, works the
same way.

### Without a Container

```java
IBrowserProperties properties = new BrowserProperties()
        .setName("chromium")
        .setHeadless(true)
        .setTimeout(5000L)
        .setPageLoadTimeout(30000L)
        .setWidth(1600)
        .setHeight(900);
IBrowserFactory browserFactory = new BrowserFactory<>(properties, new BrowserLauncherRegistry());
IBrowserService<IBrowserProperties> browserService = new BrowserService<>(properties, browserFactory);
IElementFactory elementFactory = new ElementFactory(
        new ElementFinderService(browserService),
        properties,
        new LocalizationService(new LocalizationReader(), "en", null));
IConditionalWait conditionalWait = new ConditionalWait(Duration.ofSeconds(5), Duration.ofMillis(500));

ExampleStorePage storePage = new ExampleStorePage(elementFactory);
```

Keep them in one place, e.g. fields of a base test, and use `browserService` in setup and teardown
exactly as in the Spring base test below. `WithoutSpringDemoTests` in `src/test/java/io/github/jcd2052/demo/tests`
is a running example, with the settings read from a JSON file (`JsonPropertiesDemoTests`, via
`JsonBrowserProperties`) or written in a class implementing `IBrowserProperties`
(`HardcodedPropertiesDemoTests`, via `HardcodedBrowserProperties`).

### With Guice

```java
public class FrameworkModule extends AbstractModule {
    @Provides
    @Singleton
    IBrowserProperties browserProperties() {
        return new BrowserProperties()
                .setName("chromium")
                .setHeadless(true)
                .setTimeout(5000L)
                .setPageLoadTimeout(30000L);
    }

    @Provides
    @Singleton
    IBrowserFactory browserFactory(IBrowserProperties properties) {
        return new BrowserFactory<>(properties, new BrowserLauncherRegistry());
    }

    @Provides
    @Singleton
    IBrowserService<IBrowserProperties> browserService(IBrowserProperties properties, IBrowserFactory browserFactory) {
        return new BrowserService<>(properties, browserFactory);
    }

    @Provides
    @Singleton
    IElementFactory elementFactory(IBrowserService<IBrowserProperties> browserService, IBrowserProperties properties) {
        return new ElementFactory(
                new ElementFinderService(browserService),
                properties,
                new LocalizationService(new LocalizationReader(), "en", null));
    }
}
```

Mark page constructors with `@Inject` (and the page classes with `@Singleton`), and let TestNG create the
injector with `@Guice(modules = FrameworkModule.class)` on the test class, so pages and the browser service
can be injected into the tests' fields.

### With Spring

The bundled tests wire the framework beans in a Spring configuration and scan their pages. This is a
shortened version of `src/test/java/io/github/jcd2052/configuration/SpringBootTestConfiguration.java`, which reads
every setting above:

```java
@Configuration
@ComponentScan(basePackages = {"org.example"})
public class TestConfiguration {
    @Value("${playwright.browser.headless:true}")
    private boolean isHeadless;
    @Value("${playwright.browser.name:chrome}")
    private String browserName;
    @Value("${playwright.browser.tracing:false}")
    private boolean isTracingEnabled;
    @Value("${playwright.browser.timeout:30000}")
    private long timeout;
    @Value("${playwright.browser.page.load.timeout:30000}")
    private long pageLoadTimeout;
    @Value("${playwright.browser.viewport.width:1600}")
    private Integer width;
    @Value("${playwright.browser.viewport.height:900}")
    private Integer height;
    @Value("${playwright.browser.tracing.folder:target/tracing}")
    private String tracingFolder;
    @Value("${playwright.browser.permissions:}")
    private String permissions;
    @Value("${playwright.logs.language:en}")
    private String logsLanguage;

    @Bean
    public IBrowserLauncherRegistry browserLauncherRegistry() {
        return new BrowserLauncherRegistry();
    }

    @Bean
    public IBrowserFactory browserFactory(IBrowserProperties browserProperties, IBrowserLauncherRegistry registry) {
        return new BrowserFactory<>(browserProperties, registry);
    }

    @Bean
    public IBrowserService<IBrowserProperties> browserService(
            IBrowserProperties browserProperties, IBrowserFactory browserFactory) {
        return new BrowserService<>(browserProperties, browserFactory);
    }

    @Bean
    public IElementFinderService elementFinderService(IBrowserService<IBrowserProperties> browserService) {
        return new ElementFinderService(browserService);
    }

    @Bean
    public ILocalizationService localizationService() {
        // The last argument is an optional directory with your own <language>.json files.
        return new LocalizationService(new LocalizationReader(), logsLanguage, null);
    }

    @Bean
    public IElementFactory elementFactory(
            IElementFinderService elementFinderService,
            IBrowserProperties browserProperties,
            ILocalizationService localizationService) {
        return new ElementFactory(elementFinderService, browserProperties, localizationService);
    }

    @Bean
    public IConditionalWait conditionalWait() {
        return new ConditionalWait(Duration.ofMillis(timeout), Duration.ofMillis(500));
    }

    @Bean
    public IBrowserProperties browserProperties() {
        return new BrowserProperties()
                .setHeadless(isHeadless)
                .setName(browserName)
                .setTracing(isTracingEnabled)
                .setTimeout(timeout)
                .setPageLoadTimeout(pageLoadTimeout)
                .setWidth(width)
                .setHeight(height)
                .setTracingSaveFolder(tracingFolder)
                .setPermissions(Arrays.stream(permissions.split(","))
                        .map(String::trim)
                        .filter(permission -> !permission.isEmpty())
                        .toList());
    }
}
```

Then use it from a TestNG/Spring base test:

```java
@SpringBootTest(classes = TestConfiguration.class)
public class BaseTests extends AbstractTestNGSpringContextTests {
    @Autowired
    protected IBrowserService<IBrowserProperties> browserService;

    @BeforeMethod(alwaysRun = true)
    public void setupBrowser() {
        browserService.getBrowser();
        if (browserService.isTracingEnabled()) {
            browserService.startTracing();
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            if (!result.isSuccess()) {
                browserService.getBrowser().takeScreenshot();
                if (browserService.isTracingEnabled()) {
                    browserService.stopAndSaveTrace("trace_" + result.getMethod().getMethodName() + ".zip");
                }
            }
        } finally {
            // Always close, even if collecting failure artifacts throws, so the browser and its
            // driver process are not left running.
            browserService.close();
        }
    }
}
```

`IBrowserService` in short:

* `getBrowser()` returns the thread's browser as is; when the thread has none (first use, after
  `close()`, or after a crash), it launches a new one and opens its first window.
* `close()` closes the thread's browser with all its windows and its Playwright driver process. Each thread
  owns its browser, so each thread must close its own: call it in teardown, as above.
* `setBrowser(factory)` replaces the thread's browser with one launched by another `IBrowserFactory`.
* `isTracingEnabled()` tells whether tracing is on in the settings. `startTracing()` and
  `stopAndSaveTrace(fileName)` always act on the current window, so check it first, as above.
  `stopAndSaveTrace` writes the ZIP into the tracing folder and also returns its bytes, e.g. to attach
  to a report.

### Using a custom `IBrowserProperties`

`BrowserFactory`, `BrowserService`, and `IBrowserService` are parameterized over
`T extends IBrowserProperties`, so a project with configuration needs beyond the built-in properties
(a proxy profile, an environment name, feature flags, ...) can define its own interface extending
`IBrowserProperties` and pass its implementation in. The framework itself only reads the
`IBrowserProperties` part:

```java
public interface IMyBrowserProperties extends IBrowserProperties {
    String getEnvironmentName();
}

IMyBrowserProperties properties = ...;
IBrowserService<IMyBrowserProperties> browserService =
        new BrowserService<>(properties, new BrowserFactory<>(properties, new BrowserLauncherRegistry()));
```

If you don't need anything beyond the built-in properties, use `IBrowserProperties` itself as `T`,
exactly as the example above does.

## Working With the Browser

### Browser

One per thread, from `browserService.getBrowser()`:

```java
IBrowser browser = browserService.getBrowser();

IBrowserWindow window = browser.openNewWindow();      // opens with one tab and becomes current
IBrowserWindow signedIn = browser.openNewWindow(Path.of("target/admin-state.json"));  // preloaded storage state
List<IBrowserWindow> windows = browser.getBrowserWindows();   // in no particular order
browser.switchToWindow(window);
browser.closeWindow(signedIn);                        // closes the window and all its tabs
browser.close();                                      // closes everything and stops the Playwright driver
boolean closed = browser.isClosed();
Browser playwrightBrowser = browser.getBrowser();     // the raw Playwright browser

// Shortcuts to the current window and tab
IBrowserWindow currentWindow = browser.requireCurrentBrowserWindow();
IBrowserTab currentTab = browser.requireCurrentBrowserTab();
browser.navigateTo("https://example.com/");
browser.setViewportSize(1280, 720);
byte[] screenshot = browser.takeScreenshot();
browser.closeWindow();                                // closes the current window
```

The window list has no order, so keep a window you want to come back to in a variable and switch to it
later. The `require...` methods throw an `IllegalStateException` when there is no current window or tab;
`getCurrentBrowserWindow()` returns `null` instead.

### Window

A window is a Playwright browser context: its tabs share cookies and local storage.

```java
IBrowserWindow window = browser.requireCurrentBrowserWindow();

IBrowserTab newTab = window.openNewTab();             // becomes the current tab
List<IBrowserTab> tabs = window.getBrowserTabs();     // oldest first, popups opened by the page included
window.switchToTab(tabs.get(0));
window.closeTab(newTab);                              // the newest remaining tab becomes current
window.saveStorageState(Path.of("target/admin-state.json"));   // reuse with openNewWindow(path)
window.closeWindow();
BrowserContext context = window.getBrowserContext();  // the raw Playwright context

ICookieManager cookies = window.getCookieManager();
cookies.addCookies(List.of(new Cookie("session", "abc123").setUrl("https://example.com")));
List<Cookie> all = cookies.getCookies();
cookies.clearCookies();
```

### Tab

A tab is a Playwright page, and all page-level actions live here:

```java
IBrowserTab tab = browser.requireCurrentBrowserTab();

tab.navigateTo("https://example.com/");
tab.goBack();
tab.goForward();
tab.reloadTab();
tab.waitForNetworkIdle();
String url = tab.getCurrentUrl();
String title = tab.getTitle();
tab.setViewportSize(1280, 720);
byte[] screenshot = tab.takeScreenshot();             // the whole page, as PNG
tab.closeTab();
Page page = tab.getPage();                            // the raw Playwright page

// Page-level JavaScript; scripts on one element go through element.getJsActions()
Object scrollY = tab.evaluate("window.scrollY");
Object cookieString = tab.evaluate("document.cookie");

// Web storage of the loaded page's origin
tab.getLocalStorage().setItem("theme", "dark");
String theme = tab.getLocalStorage().getItem("theme");   // null if missing
tab.getSessionStorage().clear();

// Raw mouse (viewport coordinates) and keyboard (sent to the focused element)
tab.getMouseActions().click(100, 200);
tab.getMouseActions().wheel(0, 500);
tab.getKeyboardActions().press("Control+A");
tab.getKeyboardActions().type("hello");

// Downloads
Download download = tab.waitForDownload(exportButton::click);
tab.saveFileAfterAction(exportButton::click, Path.of("target/report.csv"));

// Clipboard (needs the clipboard-read permission)
String copied = tab.readClipboard();
```

Raw mouse actions can drag along a path: `move(x, y)`, `down()`, `move(x, y, steps)` (one `mousemove` per
step) and `up()`. Raw keyboard actions also offer `insertText(text)` (no key events, like pasting) and
`down(key)`/`up(key)` to hold a key such as `Shift`.

### Alerts

`handleNextAlert` runs an action that opens a dialog (`alert`, `confirm` or `prompt`), handles the dialog
with the given handler, and returns it, so its values can be read afterwards:

```java
IAlert alert = tab.handleNextAlert(IAlert::accept, saveButton::click);
String message = alert.getMessage();                  // e.g. "Saved!"
AlertType type = alert.getType();                     // ALERT, CONFIRM, PROMPT or BEFORE_UNLOAD

tab.handleNextAlert(IAlert::decline, deleteButton::click);          // confirm() returns false
IAlert prompt = tab.handleNextAlert(dialog -> dialog.fillText("Ada"), renameButton::click);
String defaultValue = prompt.getDefaultValue();       // the prompt's pre-filled text
```

It waits, up to the default timeout, for a dialog that opens shortly after the action (e.g. from a timer),
and throws Playwright's `TimeoutError` when none opens; its handler is removed either way, so it never
catches a later dialog. The handler must accept or decline the dialog, or the page stays blocked.
Accepting a prompt without text answers it with an empty string; declining it answers with `null`.

## Creating Pages

Create pages or localized fragments by extending `AbstractForm`. Pass a stable root `Selector` identifying
the form's container. `@Component` below is how the Spring example picks pages up; with another container,
use its own annotation, or create the page with `new`.

```java
@Component
public class ExampleStorePage extends AbstractForm {

    private final ITextBoxElement searchBox;
    private final IButtonElement searchButton;

    protected ExampleStorePage(IElementFactory elementFactory) {
        super(Selector.bySelector("#global_header"), "Store page", elementFactory);

        this.searchBox = getElementFactory().createTextBoxElement(
                Selector.bySelector("form[action*='store'] input[role='combobox']"),
                "Search box");
        this.searchButton = getElementFactory().createButtonElement(
                Selector.byRole(AriaRole.BUTTON, "Search"),
                "Search button");
    }

    public void performSearch(String searchValue) {
        searchBox.clearAndFillText(searchValue);
        searchButton.click();
    }

    public String getSearchValue() {
        return searchBox.getInputValue();
    }
}
```

Keep the elements private and give tests page-level methods instead, as above. See
`src/test/java/io/github/jcd2052/demo/pages/DemoLoginPage.java` for the real, running version of this pattern.

Useful page methods from `AbstractForm`:

* `getName()`
* `isVisible()`
* `waitForLoading()` / `waitForLoading(timeout)`
* `waitToBeVisible()` / `waitToBeVisible(timeout)`
* `waitToBeInvisible()` / `waitToBeInvisible(timeout)`

## Creating Elements

Inject or inherit `IElementFactory`, then create typed elements by passing a `Selector` strategy:

```java
IButtonElement saveButton = elementFactory.createButtonElement(Selector.byRole(AriaRole.BUTTON, "Save"), "Save button");
ITextBoxElement emailInput = elementFactory.createTextBoxElement(Selector.byPlaceholder("Enter Email"), "Email input");
IDropdownElement countryDropdown = elementFactory.createDropdownElement(Selector.byLabel("Select Country"), "Country dropdown");
ICheckBoxElement termsCheckbox = elementFactory.createCheckBoxElement(Selector.bySelector("input[type='checkbox']"), "Terms checkbox");
IRadioButtonElement cardRadio = elementFactory.createRadioButtonElement(Selector.byText("Credit Card"), "Card payment");
ILabelElement errorLabel = elementFactory.createLabelElement(Selector.byTestId("error-banner"), "Error label");
ILinkElement docsLink = elementFactory.createLinkElement(Selector.bySelector("a.docs-link"), "Docs link");
IUploadBox avatarUpload = elementFactory.createUploadBoxElement(Selector.bySelector("input[type='file']"), "Avatar upload");
```

Selector strategies: `bySelector` (CSS/XPath), `byText`, `byRole`, `byLabel`, `byPlaceholder`, `byAltText`,
`byTitle`, `byTestId` and `byFrame` (an element inside an `<iframe>`); most also take an `exact` flag or a
`Pattern`. Combine them with `chain(...)`, and pick one match with `nth(index)` (0-based).

Create child elements scoped to a parent element's locator:

```java
IElement row = elementFactory.createCustomElement(LabelElement.class, Selector.bySelector("tr.user-row").chain(Selector.byText("John")), "John Row");
IButtonElement editButton = row.createChildElement(IButtonElement.class, Selector.byRole(AriaRole.BUTTON, "Edit"), "Edit Button");
```

Create dynamic item collections:

```java
IElementCollection<ILabelElement> resultTitles = elementFactory.createElementsCollection(
        Selector.bySelector("div.result-title"),
        "Result title",
        ILabelElement.class,
        ExpectedCount.MORE_THAN_ZERO);

List<ILabelElement> titles = resultTitles.getElements();
```

`ExpectedCount` options:

* `ANY` does not wait for a specific count.
* `MORE_THAN_ZERO` waits for at least one matching element to be attached.
* `ZERO` asserts that no matching elements exist.

## Element Methods

All elements implement common methods from `IElement`:

```java
element.click();
element.doubleClick();
element.forceClick();
element.rightClick();
element.middleClick();
element.click(10, 20);
element.hover();
element.press("Enter");
element.dragTo(targetElement);
element.dragAndDropTo(targetElement, 15);
element.scrollToElement();
element.unfocus();

boolean visible = element.isVisible();
boolean enabled = element.isEnabled();
String text = element.getText();
String href = element.getAttribute("href");
List<String> lines = element.getAllTexts();
byte[] screenshot = element.getScreenshot();

element.waitToBeVisible(5000.0);
element.waitToBeDetached(5000.0);
element.waitForLoading();
```

Text boxes:

```java
emailInput.clearText();
emailInput.fillText("qa@example.com");
emailInput.clearAndFillText("new@example.com");
String value = emailInput.getInputValue();
```

Dropdowns:

```java
countryDropdown.selectByText("Ukraine");
countryDropdown.selectByValue("UA");
countryDropdown.selectByIndex(2);
String selected = countryDropdown.getSelectedOption();
List<String> options = countryDropdown.getTexts();
```

Checkboxes and radio buttons:

```java
termsCheckbox.check();
termsCheckbox.uncheck();
boolean accepted = termsCheckbox.isChecked();
boolean cardSelected = cardRadio.isChecked();
```

Uploads:

```java
avatarUpload.upload(new File("src/test/resources/avatar.png"));
avatarUpload.upload(Path.of("src/test/resources/avatar.png"));
```

JavaScript on the element:

```java
element.clickWithJs();
element.getJsActions().scrollIntoView();
element.getJsActions().setAttribute("data-test", "updated");
boolean pointerEventsDisabled = element.getJsActions().isPointerEventsDisabled();
boolean fullyInView = element.getJsActions().isInViewport();
```

`element.getMouseActions()` returns the raw mouse of the element's page (viewport coordinates, the same
as `tab.getMouseActions()`), and `element.getLocator()` the raw Playwright `Locator`.

## Complex Grid and Row Automation

The framework supports structured grids out of the box with `AbstractTableGridForm` and `AbstractRow`.

Rows don't build fragile positional strings (`//tr[5]`). Instead, they wrap a generic row selector and pick
their row with Playwright's `.nth()`, so rows work with any locator strategy (CSS, roles, text, test IDs).

Row positions are **1-based** (the first row is `1`), matching how rows are numbered on screen and in
element names. Element collections and `Selector.nth(...)` are 0-based. Passing a position below 1 to
`AbstractRow` throws an `IllegalArgumentException`.

```java
public class UserRow extends AbstractRow<UserModel> {
    public UserRow(int position, IElementFactory factory) {
        super(
            position,
            Selector.bySelector("td"),                     // Cell locator
            Selector.byRole(AriaRole.ROW),                 // Generic row matching template
            "User Data Row",
            factory
        );
    }

    @Override
    public UserModel getModelFromRow() {
        List<ILabelElement> cells = getCellElements();
        return new UserModel(cells.get(0).getText(), cells.get(1).getText());
    }
}
```

A table extends `AbstractTableGridForm` and gets `getRowsFromPage()` and `getModelsFromRows()`. See
`DemoProductsPage` and `DemoProductRow` for the running version.

## Waiting for Custom Conditions

`IElement`'s waits (`waitToBeVisible()`, `waitToBeDetached()`, ...) only cover a single element.
For conditions that span multiple elements or a collection — "wait until the table has exactly N
rows", "wait until the spinner is gone *and* the results count is positive" — inject
`IConditionalWait` and express the condition as a `BooleanSupplier`:

```java
public void waitForRowCount(int expectedCount) throws TimeoutException {
    conditionalWait.waitForTrue(
            () -> getModelsFromRows().size() == expectedCount,
            "Products table never reached " + expectedCount + " row(s)");
}
```

`waitFor(...)` returns a boolean instead of throwing, for conditional logic rather than hard
assertions. Both accept an optional custom `timeout`/`pollingInterval` per call, and an optional
set of exception types to treat as "not yet satisfied" instead of propagating (useful while an
element is still attaching to the DOM). See `DemoProductsPage.waitForRowCount(...)` for the real,
running version of this pattern.

## Creating Custom Elements

Use custom elements when a reusable component has behavior that is more specific than a generic button,
label, or text box.

```java
public class PriceElement extends LabelElement {
    protected PriceElement(Selector selector, String name, IElementFactory elementFactory) {
        super(selector, name, elementFactory);
    }

    public BigDecimal getAmount() {
        return new BigDecimal(getText().replace("$", "").trim());
    }
}
```

Create it with the factory, which calls its `(Selector, String, IElementFactory)` constructor by
reflection (it may be protected):

```java
PriceElement price = elementFactory.createCustomElement(
        PriceElement.class,
        Selector.byTestId("item-price"),
        "Product price");

BigDecimal amount = price.getAmount();
```

You can also use a supplier when the element needs custom construction logic:

```java
PriceElement price = elementFactory.createCustomElement(
        (selector, name, factory) -> new PriceElement(selector, name, factory),
        Selector.byPlaceholder("Amount"),
        "Product price");
```

To have the factory create your own implementation for one of its element interfaces (also for the
`createButtonElement(...)`-style shortcuts), register it:

```java
elementFactory.registerElement(IButtonElement.class, MyButtonElement.class);
```

Asking for an interface with no registered implementation throws an `IllegalArgumentException` naming the
interface. Custom elements can still use all inherited methods such as `click()`, `getText()`, `isVisible()`,
child element creation, waits, screenshots, and JavaScript actions.

## Creating Custom Target Attributes

Because `Selector` is an open abstract strategy class, adding support for custom frontend framework
attributes (like Angular's `ng-model` or internal corporate wrappers) is straightforward without modifying
the core framework code:

```java
public class CustomSelector {
    public static Selector byNgModel(String name) {
        return Selector.bySelector(String.format("[ng-model='%s']", name));
    }
}
```

## Logs

Every element action is logged at INFO level with the element's name, e.g. `Clicked on 'Save button'`, in
the language set by `playwright.logs.language` (bundled: `en`, `uk`). To change the messages or add a
language, pass a directory to `LocalizationService` (the last constructor argument) with your own
`<language>.json`. A file found there replaces the bundled one for that language, so it needs every message
key; copy `src/main/resources/localization/en.json` as a starting point.

## Exceptions

Every action wrapped by `AbstractElement` (click, fill, waits, ...) translates raw Playwright
errors into one of two unchecked exceptions, both carrying the original exception as `getCause()`:

* `ElementTimeoutException` — the element never reached an actionable state in time.
* `ElementActionException` — the action itself failed (e.g. the element was detached from the
  DOM mid-action, or its context closed).

Both extend `BasePlaywrightException`, so you can catch broadly or specifically. The message is
compacted from Playwright's often-verbose output and always includes the element name, the action
attempted, and the `Selector` strategy that was used — e.g.:

```
Failed to perform 'click' on element 'Save button'.
Reason: Target closed
Locator Strategy: byRole(BUTTON, name="Save")
(The element was likely removed from the DOM during the action).
```

A log message file that is missing or can't be read fails with a `LocalizationException`.

## Creating Your Own Browser Settings

The usual path is property-based configuration:

```properties
playwright.browser.name=firefox
playwright.browser.headless=true
playwright.browser.timeout=30000
playwright.browser.page.load.timeout=60000
playwright.browser.viewport.width=1366
playwright.browser.viewport.height=768
playwright.browser.testid.attribute=data-test
```

For full programmatic control, create `BrowserProperties` in code instead of reading property values. With
Spring, provide it as a bean:

```java
@Configuration
public class CustomBrowserConfiguration {
    @Bean
    @Primary
    public IBrowserProperties customBrowserProperties() {
        return new BrowserProperties()
                .setName("chrome")
                .setHeadless(true)
                .setTimeout(30000L)
                .setPageLoadTimeout(60000L)
                .setWidth(1366)
                .setHeight(768)
                .setTracing(false)
                .setTracingScreenshotsEnabled(true)
                .setTracingSnapshotsEnabled(true)
                .setHighlight(false)
                .setTracingSaveFolder("target/tracing")
                .setTestIdAttribute("data-test")
                .setLocale("en-US")
                .setPermissions(List.of("clipboard-read", "clipboard-write"))
                .setArgs(List.of("--no-sandbox", "--disable-dev-shm-usage"));
    }
}
```

To add a new browser launcher, implement `IBrowserLauncher` and register it in the
`BrowserLauncherRegistry` the `BrowserFactory` is created with (here as a Spring bean):

```java
public class ChromeBetaLauncher implements IBrowserLauncher {
    @Override
    public String getName() {
        return "chrome-beta";
    }

    @Override
    public Browser launch(Playwright playwright, BrowserType.LaunchOptions options) {
        return playwright.chromium().launch(options.setChannel("chrome-beta"));
    }
}
```

```java
@Configuration
public class CustomLauncherConfiguration {
    @Bean
    @Primary
    public IBrowserLauncherRegistry customBrowserLauncherRegistry() {
        BrowserLauncherRegistry registry = new BrowserLauncherRegistry();
        registry.register(new ChromeBetaLauncher());
        return registry;
    }
}
```

## Demo With Existing Tests

The repository ships a small, fully offline demo suite instead of relying on a live third-party
site — `DemoServer` serves a handful of bundled static pages (`src/test/resources/demo-site`) on
`localhost`, so the suite is fast, deterministic, and safe to run in CI: no network access beyond
`localhost`, and nothing outside this repository can ever break these tests.

* `index.html` — a sign-in form (text inputs, checkbox, dropdown, button)
* `table.html` — a products table, exercising `AbstractRow`/`AbstractTableGridForm`
* `frame.html` + `widget.html` — a widget embedded in an `<iframe>`, exercising `Selector.byFrame`
* `elements.html` — one fixture per element type, used to test every built-in element

`DemoFrameworkShowcaseTests` drives the first three pages through their respective Page Objects
(`DemoLoginPage`, `DemoProductsPage`/`DemoProductRow`, `DemoFramePage`), and `DemoElementsTests`
exercises every element type through `DemoElementsPage`. The pages keep their elements private, and the
tests only call page methods. `SpringBootBaseTests` in the same package handles browser start-up, tracing,
and teardown (taking a screenshot and saving a trace on failure); `DemoSiteBaseTests` extends it to start
the demo server and adds `openDemoPage(...)`:

```java
/**
 * Verifies that the sign-in form submits the entered values.
 */
@Test(description = "The login form fills and submits")
public void loginFormFillsAndSubmits() {
    openDemoPage("/index.html");

    String status = loginPage.signIn("qa-engineer", "hunter2", true, "ua");

    Assert.assertEquals(status, "Welcome, qa-engineer! (remembered) [ua]");
}
```

`JsonPropertiesDemoTests` and `HardcodedPropertiesDemoTests` (sharing `WithoutSpringDemoTests`) run the same
sign-in page with no Spring at all: the framework is wired by hand, from settings read from
`src/test/resources/browser-properties.json` or written in `HardcodedBrowserProperties`, and a test checks
that those settings (viewport, locale, time zone) reached the browser.

The browser layer (`IBrowser`, `IBrowserWindow`, `IBrowserTab`, `IAlert`, `ICookieManager`) is tested
against a real headless Chromium under `src/test/java/io/github/jcd2052/browser/browser`; those tests serve
their pages through Playwright routes, so they need no network either.

Run the whole suite (unit, browser and demo tests) with:

```bash
mvn test
```

## Notes For Scaling

* Keep tests focused on page-level behavior methods, not raw element operations.
* Prefer user-facing semantic selectors such as ARIA roles or text content using `Selector.byRole()` and `Selector.byText()`.
* Keep browser settings environment-specific through properties.
* Close the browser in every test's teardown; each thread owns exactly one.
* Use `@DataProvider(parallel = true)` only when tests are independent and do not depend on shared backend state.
* Sign in once, save the window's storage state, and open later windows with it instead of repeating a UI login.
* Add custom elements for repeated domain components such as grids, rows, price blocks, date pickers, and upload widgets.
