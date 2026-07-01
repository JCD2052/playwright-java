package org.jcd2052.steam;

import org.jcd2052.steam.pages.SteamAgeCheckPage;
import org.jcd2052.steam.pages.SteamSearchResultPage;
import org.jcd2052.steam.pages.SteamStorePage;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SteamTests extends BaseTests {
    @Autowired
    private SteamStorePage storePage;
    @Autowired
    private SteamSearchResultPage searchResultPage;
    @Autowired
    private SteamAgeCheckPage ageCheckPage;

    @DataProvider(parallel = true)
    public static Object[][] dataProviderMethod() {
        return new Object[][]{
                {"Battlefield 6"}, {"Battlefield 3"}, {"Battlefield 4"}
        };
    }

    @Test(dataProvider = "dataProviderMethod")
    public void testSearch(String searchValue) {
        storePage.performSearch(searchValue);
        String valueFromSearch = storePage.getValueFromSearch();

        Assert.assertTrue(searchResultPage.getSearchTags().contains(String.format("\"%s\" ", searchValue)));
        Assert.assertTrue(valueFromSearch.isEmpty());
    }

    @Test
    public void testSearchResultPage() {
        browserService.navigateTo("https://store.steampowered.com/agecheck/app/3240220/");
        ageCheckPage.fillTheForm(30, "May", 1995);
    }
}
