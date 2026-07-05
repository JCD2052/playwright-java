package org.jcd2052.steam;

import org.jcd2052.steam.pages.SteamAgeCheckPage;
import org.jcd2052.steam.pages.SteamApplicationPage;
import org.jcd2052.steam.pages.SteamSearchResultPage;
import org.jcd2052.steam.pages.SteamStorePage;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class SteamTests extends BaseTests {
    @Autowired
    private SteamStorePage storePage;
    @Autowired
    private SteamSearchResultPage searchResultPage;
    @Autowired
    private SteamAgeCheckPage ageCheckPage;
    @Autowired
    private SteamApplicationPage steamApplicationPage;

    @BeforeMethod
    public void goToMainPage() {
        browserService.navigateTo("https://store.steampowered.com/");
    }

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
        softAssert.assertEquals(storePage.getValueFromSearch(), "");
        softAssert.assertEquals(searchResultPage.getValueFromSearch(), searchValue);
        softAssert.assertAll();
    }

    @Test
    public void testAgeCheck() {
        browserService.navigateTo("https://store.steampowered.com/agecheck/app/3240220/");
        ageCheckPage.fillTheForm(30, "May", 1995);

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(steamApplicationPage.waitForLoading());
        softAssert.assertEquals(steamApplicationPage.getApplicationName(), "Grand Theft Auto V Enhanced");
        softAssert.assertAll();
    }
}
