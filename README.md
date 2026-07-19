# Playwright Java Automation Framework

A lightweight UI automation framework for Java web tests built on top of Playwright.
The framework is organized around Page Objects, typed element wrappers, Spring dependency injection, and TestNG execution.

## Tools Used

* Java 17
* Playwright for Java
* Spring Boot for dependency injection and property-based configuration (for test purposes, is not used in core framework)
* TestNG for test execution and data-driven parallel runs
* Maven for dependency and build management
* Lombok for simple model classes
* Logback for framework logging

## How It Works

The framework keeps Page Objects and Elements small. Page classes describe the screen, elements wrap Playwright locators, and tests call page-level business methods.

Elements store a custom `Selector` object wrapper strategy and resolve the live Playwright `Locator` dynamically at the exact millisecond an action is executed. This Just-In-Time (JIT) architecture prevents `StaleElementReferenceException` errors and keeps tests resilient against modern single-page applications (SPAs) where the DOM dynamically updates.

Browser state is managed via the `BrowserService`. Each execution thread gets its own browser through `ThreadLocal`, and each started test opens a fresh Browser Context and Page. That allows TestNG data providers such as `@DataProvider(parallel = true)` to run isolated sessions in parallel without sharing cookies, cache, local storage, or active pages.

## Project Structure

```text
src/main/java/org/jcd2052/core
  browser/       Browser lifecycle, tabs, windows, launchers, configuration
  elements/      Typed element wrappers, interfaces, and dynamic selectors
  pages/         Base form/page abstractions, grids, and index-bound rows
  waiting/       Generic conditional wait utility
  logger/        Logging abstraction

src/test/java/org/jcd2052/steam
  configuration/ Example Spring test configuration
  pages/         Example Steam Page Objects using Selector factories
  springboottests/ Example TestNG tests
```

## Browser Settings

Browser configuration is read from Spring properties. The example values live in `src/test/resources/application.properties`.

```properties
playwright.browser.headless=false
playwright.browser.name=chrome
playwright.browser.tracing=false
playwright.browser.timeout=50000
playwright.browser.page.load.timeout=1200000
playwright.browser.highlight=false
playwright.browser.screenshots=true
playwright.browser.snapshots=true
playwright.browser.tracing.folder=target/tracing
playwright.browser.viewport.width=1600
playwright.browser.viewport.height=900
playwright.browser.testid.attribute=data-a-test
playwright.browser.tracing.args=--no-sandbox,--disable-dev-shm-usage,--disable-gpu
```

Supported browser names:

* `chrome`
* `firefox`
* `edge`
* `webkit`

Important settings:

* `playwright.browser.headless` controls visible vs headless browser execution.
* `playwright.browser.name` selects the browser launcher.
* `playwright.browser.timeout` sets the default Playwright action timeout on created contexts.
* `playwright.browser.page.load.timeout` sets the default navigation timeout.
* `playwright.browser.viewport.width` and `playwright.browser.viewport.height` set context viewport size.
* `playwright.browser.tracing` enables Playwright tracing.
* `playwright.browser.screenshots` and `playwright.browser.snapshots` control trace detail.
* `playwright.browser.tracing.folder` controls where trace ZIP files are written.
* `playwright.browser.highlight` highlights elements before supported actions.
* `playwright.browser.testid.attribute` overrides the default `data-testid` attribute globally for `Selector.byTestId()`.
* `playwright.browser.tracing.args` passes comma-separated launch arguments to the browser.

## Spring Configuration

For tests, import the framework configuration and scan your own test pages:

```java
package org.example.configuration;

import org.jcd2052.core.browser.configuration.PlaywrightFrameworkConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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
    @Value("${playwright.browser.tracing.folder:target/tracing}")
    private String tracingFolder;
    @Value("${playwright.browser.highlight:false}")
    private boolean highlight;
    @Value("${playwright.browser.screenshots:true}")
    private boolean screenshots;
    @Value("${playwright.browser.snapshots:true}")
    private boolean snapshots;
    @Value("${playwright.browser.viewport.width:1600}")
    private Integer width;
    @Value("${playwright.browser.viewport.height:900}")
    private Integer height;
    @Value("${playwright.browser.page.load.timeout:30000}")
    private long pageLoadTimeout;
    @Value("${playwright.browser.testid.attribute:data-testid}")
    private String testIdAttribute;
    @Value("${playwright.browser.tracing.args:}")
    private String args;

    @Bean
    public IBrowserLauncherRegistry browserLauncherRegistry() {
        return new BrowserLauncherRegistry();
    }

    @Bean
    public IBrowserFactory browserFactory(IBrowserProperties browserProperties, IBrowserLauncherRegistry registry) {
        return new BrowserFactory(browserProperties, registry);
    }

    @Bean
    public IElementFactory elementFactory(
            IElementFinderService elementFinderService,
            IBrowserProperties browserProperties) {
        return new ElementFactory(elementFinderService, browserProperties);
    }

    @Bean
    public IElementFinderService elementFinderService(IBrowserService browserService) {
        return new ElementFinderService(browserService);
    }

    @Bean
    public IBrowserService browserService(IBrowserProperties browserProperties, IBrowserFactory browserFactory) {
        return new BrowserService(browserProperties, browserFactory);
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
                .setHighlight(highlight)
                .setScreenshots(screenshots)
                .setSnapshots(snapshots)
                .setTestIdAttribute(testIdAttribute)
                .setArgs(Arrays.stream(args.split(","))
                        .map(String::trim)
                        .filter(arg -> !arg.isEmpty())
                        .toList());
    }
}
```

Then use it from a TestNG/Spring base test:

```java
@SpringBootTest(classes = TestConfiguration.class)
public class BaseTests extends AbstractTestNGSpringContextTests {
    @Autowired
    protected IBrowserService browserService;

    @BeforeMethod(alwaysRun = true)
    public void setupBrowser() {
        browserService.start();
        browserService.startTracing();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (!result.isSuccess()) {
            browserService.getBrowser().takeScreenshot();
            browserService.stopAndSaveTrace("trace_" + result.getMethod().getMethodName() + ".zip");
        }
        browserService.close();
    }
}
```

## Creating Pages

Create pages or localized fragments by extending `AbstractForm`. Pass a stable root `Selector` to represent the form container wrapper.

```java
@Component
public class SteamStorePage extends AbstractForm {

    private final ITextBoxElement searchBox;
    private final IButtonElement searchButton;

    protected SteamStorePage(IElementFactory elementFactory) {
        super(Selector.bySelector("#global_header"), "Store page", elementFactory);

        // Target sub-components relative to their exact parent DOM sections seamlessly
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

Useful page methods from `AbstractForm`:

* `isVisible()`
* `waitForLoading()`
* `waitForLoading(timeout)`
* `waitToBeVisible(timeout)`
* `waitToBeInvisible(timeout)`

## Creating Elements

Inject or inherit `IElementFactory`, then pass fluid JIT `Selector` choices to create elements:

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

Create nested child elements safely relative to a parent element's locator boundaries:

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
```

JavaScript fallbacks:

```java
element.clickWithJs();
element.getJsActions().scrollIntoView();
element.getJsActions().setAttribute("data-test", "updated");
boolean pointerEventsDisabled = element.getJsActions().isPointerEventsDisabled();
```

Browser helpers:

```java
browserService.navigateTo("https://store.steampowered.com/");
browserService.getBrowser().reload();
browserService.getBrowser().setViewportSize(1280, 720);
browserService.getBrowser().takeScreenshot();
browserService.getBrowser().openNewWindow().openNewTab();
```

Tab helpers:

```java
IBrowserTab tab = browserService.getBrowser()
        .getCurrentBrowserWindow()
        .getCurrentBrowserTab();

tab.waitForNetworkIdle();
tab.reloadTab();
tab.goBack();
tab.goForward();
String url = tab.getCurrentUrl();
String title = tab.getTitle();
```

## Complex Grid and Row Automation

The framework supports structured grid layout configurations completely out of the box using `AbstractTableGridForm` and `AbstractRow`.

Row generation completely avoids fragile string concatenation (`//tr[5]`). Instead, rows natively wrap generic structural definitions using Playwright's `.nth()` filtering methods. This makes rows compatible with any locator strategy (CSS, roles, text, test-IDs).

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

## Creating Custom Elements

Extend existing classes when a reusable component has behavior that is more specific than a generic layout box.

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

Create it using the reflection engine:

```java
PriceElement price = elementFactory.createCustomElement(
        PriceElement.class,
        Selector.byTestId("item-price"),
        "Product price");

BigDecimal amount = price.getAmount();
```

Alternatively, pass explicit instantiation instructions block closures directly via suppliers:

```java
PriceElement price = elementFactory.createCustomElement(
        (selector, name, factory) -> new PriceElement(selector, name, factory),
        Selector.byPlaceholder("Amount"),
        "Product price");
```

## Creating Custom Target Attributes

Because `Selector` is an open abstract strategy class, adding support for custom frontend framework attributes (like Angular's `ng-model` or internal corporate wrappers) is straightforward without modifying the core framework code:

```java
public class CustomSelector {
    public static Selector byNgModel(String name) {
        return Selector.bySelector(String.format("[ng-model='%s']", name));
    }
}
```

## Demo With Existing Tests

The repository contains a small Steam demo suite:

* `SpringBootBaseTests` starts a browser before each method, starts tracing, saves artifacts on failure, and closes the browser.
* `SpringBootSteamTests.testSearch` uses a parallel TestNG data provider to search for several games.
* `SpringBootSteamTests.testAgeCheck` opens an age-check page, fills dropdowns, and verifies the product page.

Example from the current suite:

```java
@DataProvider(parallel = true)
public Object[][] dataProviderMethod() {
    return new Object[][]{
            {"Battlefield 6"}, {"Battlefield 3"}, {"Battlefield 4"}
    };
}

@Test(dataProvider = "dataProviderMethod")
public void testSearch(String searchValue) {
    storePage.performSearch(searchValue);

    SoftAssert softAssert = new SoftAssert();
    softAssert.assertTrue(searchResultPage.getSearchTags().contains(String.format("\"%s\" ", searchValue)));
    softAssert.assertEquals(searchResultPage.getValueFromSearch(), searchValue);
    softAssert.assertAll();
}
```

Age-check example:

```java
@Test
public void testAgeCheck() {
    browserService.navigateTo("https://store.steampowered.com/agecheck/app/3240220/");
    ageCheckPage.fillTheForm(30, "May", 1995);

    SoftAssert softAssert = new SoftAssert();
    softAssert.assertTrue(steamApplicationPage.waitForLoading());
    softAssert.assertEquals(steamApplicationPage.getApplicationName(), "Grand Theft Auto V Enhanced");
    softAssert.assertAll();
}
```

## Notes For Scaling

* Keep tests focused on page-level behavior methods, not raw element operations.
* Prefer user-facing semantic selectors such as ARIA roles or text content using `Selector.byRole()` and `Selector.byText()`.
* Keep browser settings environment-specific through properties.
* Use `@DataProvider(parallel = true)` only when tests are independent and do not depend on shared backend state.
* Add custom elements for repeated domain components such as grids, rows, price blocks, date pickers, and upload widgets.