import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Holder {
    public WebDriver driver;

    @BeforeSuite
    public void startSuite() throws Exception {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/drivers/chromedriver.exe");
        driver =  new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterSuite
    public void stopSuite() throws Exception {
        System.out.println("All done!!!");
    }

    @BeforeClass
    public void startClass() throws Exception{
    }

    @AfterClass
    public void stopClass(){
//        driver.quit();
    }

    @BeforeMethod()
    public void beforeMethod() throws Exception {
    }

    @AfterMethod()
    public void afterMethod(){
    }

    @Test(priority = 1)
    public void goToGoogle() throws Exception {
        driver.get("https://www.google.com");
    }

    @Test()
    public void verifyGoogleTitle() throws Exception{
        Assert.assertEquals(driver.getTitle(), "Google");
    }

    @Test
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

    @Test
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

    @Test
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
        List<WebElement> damages = driver.findElements(By.xpath("//span[@data-uname='lotsearchLotdamagedescription']"));
        Map<String, Integer> damageTypes = new HashMap<>();
        int rearEnd = 0, frontEnd = 0, minorDent = 0, undercarriage = 0, misc = 0;
        for (WebElement damage : damages) {
            String damageType = damage.getAttribute("innerText");
            switch (damageType) {
                case "REAR END" -> damageTypes.put("REAR END", frontEnd = frontEnd + 1);
                case "FRONT END" -> damageTypes.put("FRONT END", rearEnd = rearEnd + 1);
                case "MINOR DENT/SCRATCHES" -> damageTypes.put("MINOR DENT/SCRATCHES", minorDent = minorDent + 1);
                case "UNDERCARRIAGE" -> damageTypes.put("UNDERCARRIAGE", undercarriage = undercarriage + 1);
                default -> damageTypes.put("MISC", misc = misc + 1);
            }
        }

        System.out.println("\nPART 2: DAMAGES");
        for (Map.Entry<String, Integer> m : damageTypes.entrySet()) {
            System.out.println(m.getKey() + " - " + m.getValue());
        }
    }

    @Test
    public void takeScreenshotOnException() throws IOException {
        driver.navigate().to("https://copart.com");
        searchForCars("porsche");
        waitForTableLoad(20);

        WebElement modelFilterBtn = driver.findElement(By.xpath("//a[@data-uname='ModelFilter']"));
        modelFilterBtn.click();

        WebElement searchModelFilterTbx = driver.findElement(By.xpath("//a[@data-uname='ModelFilter']/ancestor::li//form//input"));
        searchModelFilterTbx.sendKeys("skyline");

        WebElement skylineCbx;
        try {
            skylineCbx = driver.findElement(By.xpath("//a[@data-uname='ModelFilter']/ancestor::li//ul//input"));
            skylineCbx.click();
            System.out.println("Nissan Skyline found.");
        } catch (Exception e) {
            takeScreenshot("src/main/resources/screenshots/error.png");
            System.out.println("Nissan Skyline not found.");
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void create2DimensionalArrays() {
        driver.navigate().to("https://copart.com");

        List<WebElement> makes = driver.findElements(By.xpath("//span[@class='make-items']//a"));
        Map<String, String> makeUrls = new HashMap<>();
        for (WebElement element : makes) {
            makeUrls.put(element.getText(), element.getAttribute("href"));
        }

        for (Map.Entry<String, String> entry : makeUrls.entrySet()) {
            System.out.println("Make: " + entry.getKey() + ", URL: " + entry.getValue());
            Assert.assertTrue(entry.getValue().contains(entry.getKey().replaceAll(" ", "%20")));
        }

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

    public void takeScreenshot(String filepath) throws IOException {
        TakesScreenshot screenshot = ((TakesScreenshot) driver);
        File image = screenshot.getScreenshotAs(OutputType.FILE);
        File destination = new File(filepath);
        FileUtils.copyFile(image, destination);
    }
}
