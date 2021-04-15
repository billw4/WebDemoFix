package challenge1;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Challenges {

    public WebDriver driver;

    @BeforeSuite
    public void startSuite() throws Exception {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/drivers/chromedriver.exe");
        driver =  new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, SECONDS);
    }

    @AfterSuite
    public void stopSuite() throws Exception {
        System.out.println("All done!!!");
    }

    @BeforeClass
    public void startClass() {
    }

    @AfterClass
    public void stopClass(){
//        driver.quit();
    }

    @BeforeMethod()
    public void beforeMethod() {
    }

    @AfterMethod()
    public void afterMethod(){
    }

    @Test(priority = 1)
    public void goToGoogle() {
        driver.get("https://www.google.com");
    }

    @Test(priority = 2)
    public void verifyGoogleTitle() {
        Assert.assertEquals(driver.getTitle(), "Google");
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
        Assert.assertTrue(driver.getCurrentUrl().contains("exotics"));

        WebElement lastItemInTable = driver.findElement(By.xpath("//*[@id='serverSideDataTable']//tbody/tr[20]"));

        List<WebElement> exotics = driver.findElements(By.xpath("//span[@data-uname='lotsearchLotmake']"));
        List<String> exoticCars = new ArrayList<>();
        for (WebElement element : exotics) {
            exoticCars.add(element.getText());
        }
        Assert.assertTrue(exoticCars.contains("PORSCHE"));
    }

    /**
     * Challenge 3
     */
    @Test(priority = 4)
    public void getListOfPopularMakeUrls() {
        driver.navigate().to("https://copart.com");
        List<WebElement> makes = driver.findElements(By.xpath("//span[@class='make-items']//a"));
        Map<String, String> makeUrls = new HashMap<>();
        for (WebElement element : makes) {
            makeUrls.put(element.getText(), element.getAttribute("href"));
        }

        for (Map.Entry<String,String> m : makeUrls.entrySet()) {
            System.out.println(m.getKey() +" - "+ m.getValue());
        }
    }

    /**
     * Challenge 5
     */
    @Test(priority = 3)
    public void findPorscheModelsAndDamages() {
        driver.navigate().to("https://copart.com");
        searchForCars("porsche");

        Select entriesPerPage = new Select(driver.findElement(By.name("serverSideDataTable_length")));
        entriesPerPage.selectByValue("100");

        // Part 1
        waitForTableLoad(100);
        List<WebElement> models = driver.findElements(By.xpath("//span[@data-uname='lotsearchLotmodel']"));
        List<String> porscheModels = models.stream().map(WebElement::getText).sorted().collect(Collectors.toList());
        Map<String, Integer> modelCounts = new HashMap<>();

        for (String model : porscheModels) {
            if (modelCounts.containsKey(model)) {
                modelCounts.put(model, modelCounts.get(model) + 1);
            } else {
                modelCounts.put(model, 1);
            }
        }

        System.out.println("PART 1: MODELS");
        for (Map.Entry<String, Integer> m : modelCounts.entrySet()) {
            if (!m.getKey().equals("")) {
                System.out.println(m.getKey() + " - " + m.getValue());
            }
        }

        // Part 2


    }

    private void searchForCars(String car) {
        WebElement searchBarTbx = driver.findElement(By.id("input-search"));
        WebElement searchBtn = driver.findElement(By.xpath("//button[@data-uname='homepageHeadersearchsubmit']"));

        searchBarTbx.sendKeys(car);
        searchBtn.click();
    }

    private void waitForTableLoad(int rows) {
        String xpath = String.format("//*[@id='serverSideDataTable']//tbody/tr[%s]", rows);
        WebElement lastItemInTable = driver.findElement(By.xpath(xpath));
    }

}
