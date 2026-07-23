# Playwright-Java framework

A lightweight Java UI automation framework built on [Playwright](https://playwright.dev/java/), Spring, and TestNG.

The framework is organized around Page Objects, typed element wrappers, and Spring dependency
injection, with a Just-In-Time (`Selector`) locator strategy at its core — locators are resolved
fresh at the moment of every action, instead of being cached as raw strings or eagerly-resolved
`Locator` objects. That makes Page Objects safe to use as Spring singletons across parallel,
thread-isolated browser sessions.

## Project structure

```
src/main/java/org/jcd2052/core/
  browser/
    browser/          BrowserWindow, BrowserTab, PlaywrightBrowser, MouseActions, CookieManager, ...
    configuration/     IBrowserProperties / BrowserProperties
    factory/           IBrowserFactory / BrowserFactory (translates properties into launch options)
    launcher/          IBrowserLauncher implementations (Chrome, Firefox, Edge, WebKit) + registry
    services/          BrowserService, ElementFactory, ElementFinderService (JIT resolution)
  elements/            Concrete element types (ButtonElement, TextBoxElement, DropdownElement, ...)
    selector/          Selector (JIT locator strategy) and RoleOptions
  pages/               AbstractForm, and table/grid support (AbstractRow, AbstractTableGridForm)
  waiting/             ConditionalWait, a generic polling utility
  logger/               Framework-level logging abstraction over SLF4J

src/test/java/org/jcd2052/
  configuration/       SpringBootTestConfiguration — wires all framework beans from properties
  demo/
    pages/             Example Page Objects (login form, products table, iframe widget)
    support/           DemoServer — bundled static file server for the demo pages
    tests/             SpringBootBaseTests (browser lifecycle) + DemoFrameworkShowcaseTests
```

## Requirements

* Java 17+
* Maven

```xml
<dependency>
    <groupId>com.microsoft.playwright</groupId>
    <artifactId>playwright</artifactId>
    <version>1.61.0</version>
</dependency>
```

## Core concept: `Selector`

`Selector` is an abstract, Just-In-Time locator strategy. Instead of storing a raw string or a
resolved `Locator`, a `Selector` knows *how* to resolve itself — against the root `Page`, against
a parent `Locator`, or inside an `<iframe>` — at the exact moment an action runs:

```java
Selector searchBox = Selector.byTestId("search-input");
Selector saveButton = Selector.byRole(AriaRole.BUTTON, "Save");
Selector heading    = Selector.byText("Welcome back", true); // exact match
Selector byCss       = Selector.bySelector("form[action*='login'] input");
```

Available strategies mirror Playwright's own `getBy*` methods: `bySelector` (CSS/XPath),
`byText`, `byRole` (with an optional `RoleOptions` for advanced ARIA matching), `byLabel`,
`byPlaceholder`, `byAltText`, `byTitle`, and `byTestId`. Every `String` variant also has a
`Pattern` (regex) overload, and most have an `exact` boolean overload.

**Chaining** scopes a child strategy to whatever the parent resolves to:

```java
Selector row = Selector.byTestId("product-row").nth(2);
Selector removeButton = row.chain(Selector.bySelector("button.remove"));
```

**iframes** are supported directly — `byFrame` crosses into the frame and resolves the inner
strategy against its document, and frames can be nested by passing another `byFrame(...)` as the
inner selector:

```java
Selector confirmButton = Selector.byFrame(
        "#payment-widget",
        Selector.byRole(AriaRole.BUTTON, "Confirm"));
```

Every `Selector` also has a human-readable `toString()` (e.g. `byRole(BUTTON, name="Save")`,
`byFrame("#payment-widget") -> byRole(BUTTON, name="Confirm")`), which is useful in logs and
debugging when a locator stops matching.

## Creating elements

Inject or inherit `IElementFactory`, then create typed elements by passing a `Selector` strategy:

```java
@Component
public class LoginForm extends AbstractForm {
    private final ITextBoxElement usernameInput;
    private final ITextBoxElement passwordInput;
    private final IButtonElement signInButton;

    protected LoginForm(IElementFactory elementFactory) {
        super(Selector.bySelector("#login-form"), "Login form", elementFactory);

        this.usernameInput = getElementFactory().createTextBoxElement(
                Selector.byTestId("username-input"), "Username input");
        this.passwordInput = getElementFactory().createTextBoxElement(
                Selector.byTestId("password-input"), "Password input");
        this.signInButton = getElementFactory().createButtonElement(
                Selector.byRole(AriaRole.BUTTON, "Sign In"), "Sign In button");
    }

    public void signIn(String username, String password) {
        usernameInput.clearAndFillText(username);
        passwordInput.clearAndFillText(password);
        signInButton.click();
    }
}
```

Built-in element types: `IButtonElement`, `ITextBoxElement`, `ILabelElement`, `IDropdownElement`,
`ICheckBoxElement`, `IRadioButtonElement`, `ILinkElement`, `IUploadBox`. All of them expose the
common `IElement` surface — `click()`, `doubleClick()`, `rightClick()`, `middleClick()`,
`hover()`, `dragTo(...)`, `getText()`, `getAttribute(...)`, `isVisible()`, `isEnabled()`,
`waitToBeVisible()`/`waitToBeDetached()` (with or without an explicit timeout), `getScreenshot()`,
and JS-action fallbacks via `getJsActions()`.

Create child elements scoped to a parent element's locator:

```java
IButtonElement removeButton = row.createChildElement(
        IButtonElement.class, Selector.bySelector("button.remove"), "Remove button");
```

## Useful page methods from `AbstractForm`

* `getName()`
* `isVisible()`
* `waitForLoading()` / `waitForLoading(timeout)`
* `waitToBeVisible()` / `waitToBeVisible(timeout)`
* `waitToBeInvisible()` / `waitToBeInvisible(timeout)`

## Complex grid and row automation

`AbstractRow`/`AbstractTableGridForm` avoid fragile string-index locators (`//tr[5]`) entirely —
each row gets its own lazily-evaluated `Selector.nth(index)`, and rows are exposed both as
interactive Page Objects and as plain data models:

```java
public class ProductRow extends AbstractRow<Product> {
    private final ILabelElement nameCell;
    private final ILabelElement priceCell;
    private final IButtonElement removeButton;

    protected ProductRow(int position, Selector cellLocator, Selector rowLocator, IElementFactory elementFactory) {
        super(position, cellLocator, rowLocator, "Product row " + position, elementFactory);

        this.nameCell = getFormLabel().createChildElement(ILabelElement.class, Selector.bySelector("td.name"), "Name cell");
        this.priceCell = getFormLabel().createChildElement(ILabelElement.class, Selector.bySelector("td.price"), "Price cell");
        this.removeButton = getFormLabel().createChildElement(IButtonElement.class, Selector.bySelector("button.remove"), "Remove button");
    }

    public void remove() {
        removeButton.click();
    }

    @Override
    public Product getModelFromRow() {
        return new Product(nameCell.getText(), new BigDecimal(priceCell.getText().replace("$", "")));
    }
}

@Component
public class ProductsPage extends AbstractTableGridForm<Product, ProductRow> {
    private static final Selector ROW_LOCATOR = Selector.byTestId("product-row");
    private static final Selector CELL_LOCATOR = Selector.bySelector("td");

    protected ProductsPage(IElementFactory elementFactory) {
        super(Selector.byTestId("products-table"), ROW_LOCATOR, "Products", elementFactory);
    }

    @Override
    public ProductRow getRow(int index) {
        return new ProductRow(index, CELL_LOCATOR, ROW_LOCATOR, getElementFactory());
    }
}
```

`ITableGridForm` gives you `getModelsFromRows()`, `findModel(predicate)`, `getRowOrThrow(predicate)`,
and friends, so tests read and assert against plain data models without ever touching a `Locator`
directly. See `src/test/java/org/jcd2052/demo/pages/DemoProductsPage.java` and
`DemoProductRow.java` for the real, running version of this pattern.

## Creating custom elements

Use custom elements when a reusable component has behavior that is more specific than a generic
button, label, or text box.

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

Create it with the factory, which resolves the constructor via reflection (every custom element
must expose a `(Selector, String, IElementFactory)` constructor):

```java
PriceElement price = elementFactory.createCustomElement(
        PriceElement.class, Selector.byTestId("item-price"), "Product price");
```

You can also use a supplier when the element needs custom construction logic:

```java
PriceElement price = elementFactory.createCustomElement(
        (selector, name, factory) -> new PriceElement(selector, name, factory),
        Selector.byPlaceholder("Amount"),
        "Product price");
```

Custom elements can still use all inherited methods such as `click()`, `getText()`, `isVisible()`,
child element creation, waits, screenshots, and JavaScript actions.

## Creating custom target attributes

Because `Selector` is an open abstract strategy class, adding support for custom frontend
framework attributes (like Angular's `ng-model` or internal corporate wrappers) is straightforward
without modifying the core framework code:

```java
public class CustomSelector {
    public static Selector byNgModel(String name) {
        return Selector.bySelector(String.format("[ng-model='%s']", name));
    }
}
```

## Session reuse (storage state)

Playwright can only apply a saved storage state (cookies + local storage) when a *new* browser
window/context is created — there's no way to inject it into an already-open one. The framework
exposes both halves of that workflow:

```java
// Once, after a real UI login:
browser.getCurrentBrowserWindow().saveStorageState(Path.of("target/storage-state.json"));

// Later, in any test — open a window already signed in:
IBrowserWindow window = browser.openNewWindow(Path.of("target/storage-state.json"));
```

`openNewWindow(Path)` overrides `IBrowserProperties.getStorageStatePath()` for that one window
only, so different windows in the same test can each start signed in as a different user.

## Creating your own browser settings

The usual path is property-based configuration:

```properties
playwright.browser.name=chrome
playwright.browser.headless=true
playwright.browser.timeout=30000
playwright.browser.page.load.timeout=60000
playwright.browser.viewport.width=1600
playwright.browser.viewport.height=900
playwright.browser.tracing.args=--no-sandbox,--disable-dev-shm-usage

# Optional emulation / session settings (all unset by default, framework falls back to
# Playwright's own defaults for each):
playwright.browser.testid.attribute=data-test
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
```

For full programmatic control, provide your own `IBrowserProperties` bean in a custom Spring
configuration instead of relying on property values:

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
                .setWidth(1600)
                .setHeight(900)
                .setTestIdAttribute("data-test")
                .setArgs(List.of("--no-sandbox", "--disable-dev-shm-usage"));
    }
}
```

To add a new browser launcher, implement `IBrowserLauncher` and register it in a custom
`IBrowserLauncherRegistry` bean:

```java
public class ChromiumLauncher implements IBrowserLauncher {
    @Override
    public String getName() {
        return "chromium";
    }

    @Override
    public Browser launch(Playwright playwright, BrowserType.LaunchOptions options) {
        return playwright.chromium().launch(options);
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
        registry.register(new ChromiumLauncher());
        return registry;
    }
}
```

## Demo suite

The repository ships a small, fully offline demo suite instead of relying on a live third-party
site — `DemoServer` (`src/test/java/org/jcd2052/demo/support`) serves a handful of bundled static
pages (`src/test/resources/demo-site`) on `localhost`, so the suite is fast, deterministic, and
safe to run in CI: no network access beyond `localhost`, and nothing outside this repository can
ever break these tests.

* `index.html` — a sign-in form (text inputs, checkbox, dropdown, button)
* `table.html` — a products table, exercising `AbstractRow`/`AbstractTableGridForm`
* `frame.html` + `widget.html` — a widget embedded in an `<iframe>`, exercising `Selector.byFrame`

`DemoFrameworkShowcaseTests` (`src/test/java/org/jcd2052/demo/tests`) drives all three pages
through their respective Page Objects (`DemoLoginPage`, `DemoProductsPage`/`DemoProductRow`,
`DemoFramePage`). `SpringBootBaseTests` in the same package handles browser start-up, tracing, and
teardown (taking a screenshot and saving a trace on failure) and is the base class for any new
test suite.

## Testing the framework itself

`Page`, `Locator`, `FrameLocator`, `Playwright`, `BrowserType`, and `Browser` are all plain
interfaces in the Playwright Java client, so the framework's own core logic is unit-testable with
Mockito, without a real browser:

* `SelectorTest` — every `byX` strategy, `chain()`, `nth()`, `byFrame()` (including nesting), and `toString()`
* `ElementFinderServiceTest` — the JIT resolution chain from `IBrowserService` down to `Page`
* `ElementFactoryTest` — reflection-based element instantiation and the interface registry
* `BrowserFactoryTest` — launch-option translation and the unsupported-browser-name error path
* `BrowserLauncherRegistryTest` — default browser registrations and case-insensitive lookup

## Extending the framework

* Add new element types by implementing `IElement` (or extending `AbstractElement`) and
  registering the mapping via `ElementFactory.registerElement(...)`.
* Add new locator strategies as static methods on `Selector`, or external helper classes like the
  `CustomSelector.byNgModel` example above.
* Add new browser launchers by implementing `IBrowserLauncher` and registering them in a custom
  `IBrowserLauncherRegistry` bean.
* Add custom elements for repeated domain components such as grids, rows, price blocks, date
  pickers, and upload widgets.
