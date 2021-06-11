package challenges;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import utils.Helpers;

import java.util.ArrayList;
import java.util.List;

public class Challenge2 {

    public WebDriver driver;
    public WebDriverWait wait;
    private Helpers helpers;

    @BeforeSuite
    public void startSuite() throws Exception {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/drivers/chromedriver.exe");
        driver =  new ChromeDriver();
        driver.manage().window().maximize();
//        driver.manage().timeouts().implicitlyWait(10, SECONDS);
        wait = new WebDriverWait(driver, 10);
        helpers = new Helpers();
    }

    /**
     * Challenge 2
     */
    @Test(priority = 3)
    public void verifyListOfExoticCarsHasPorsche() throws InterruptedException {
        driver.navigate().to("https://copart.com");
        WebElement searchBarTbx = driver.findElement(By.id("input-search"));
        WebElement searchBtn = driver.findElement(By.xpath("//button[@data-uname='homepageHeadersearchsubmit']"));

        searchBarTbx.sendKeys("exotics");
        searchBtn.click();
        WebElement searchResultsHeader = driver.findElement(By.xpath("//h1[@data-uname='searchResultsHeader']"));
        Assert.assertTrue(searchResultsHeader.getText().contains("Search Results for exotics"));//  driver.getCurrentUrl().contains("exotics"));

        helpers.waitForSpinnerToClose(wait);

        List<WebElement> exotics = driver.findElements(By.xpath("//span[@data-uname='lotsearchLotmake']"));
        List<String> exoticCars = new ArrayList<>();
//        for(int i=0; i<exotics.size(); i++) {
//            exotics.get(i).getText();
//        }
        for (WebElement element : exotics) {
            exoticCars.add(element.getText());
        }

        Assert.assertTrue(exoticCars.contains("PORSCHE"));
    }

    @AfterClass
    public void stopClass(){
//        driver.quit();
    }


}
