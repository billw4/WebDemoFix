package challenges;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Challenges {

    public WebDriver driver;
    public WebDriverWait wait;

    @BeforeSuite
    public void startSuite() throws Exception {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/drivers/chromedriver.exe");
        driver =  new ChromeDriver();
        driver.manage().window().maximize();
//        driver.manage().timeouts().implicitlyWait(10, SECONDS);
        wait = new WebDriverWait(driver, 10);
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
        driver.quit();
    }

    @BeforeMethod()
    public void beforeMethod() {
        driver.navigate().to("https://copart.com");

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
        waitForSpinnerToClose();
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
        List<String> types = new ArrayList<>();
        for (WebElement type : damages) {
            types.add(type.getText());
        }

        int rearEnd = 0, frontEnd = 0, minorDent = 0, undercarriage = 0, misc = 0;


//        for (WebElement damage : damages) {
        for (int i = 0; i < damages.size(); i++) {
            String damageType =  damages.get(i).getText();  //.getText(); //damage.getAttribute("innerText");
            switch (damageType.toUpperCase()) {
                case "REAR END" -> rearEnd = rearEnd + 1;
                case "FRONT END" -> frontEnd = frontEnd + 1;
                case "MINOR DENT/SCRATCHES" -> minorDent = minorDent + 1;
                case "UNDERCARRIAGE" -> undercarriage = undercarriage + 1;
                default -> misc = misc + 1;
//                case "REAR END" -> damageTypes.put("REAR END", rearEnd = rearEnd + 1);
//                case "FRONT END" -> damageTypes.put("FRONT END", frontEnd = frontEnd + 1);
//                case "MINOR DENT/SCRATCHES" -> damageTypes.put("MINOR DENT/SCRATCHES", minorDent = minorDent + 1);
//                case "UNDERCARRIAGE" -> damageTypes.put("UNDERCARRIAGE", undercarriage = undercarriage + 1);
//                default -> damageTypes.put("MISC", misc = misc + 1);
//            }
            }
        }

        System.out.println("\nREAR END: " + rearEnd);
        System.out.println("FRONT END: " + frontEnd);
        System.out.println("MINOR DENT/SCRATCHES: " + minorDent);
        System.out.println("UNDERCARRIAGE: " + undercarriage);
        System.out.println("MISC: " + misc);

//        System.out.println("\nPART 2: DAMAGES");
//        for (Map.Entry<String, Integer> m : damageTypes.entrySet()) {
//            System.out.println(m.getKey() + " - " + m.getValue());
//        }

    }

    /**
     * Challenge 6
     */
    @Test
    public void challenge6() throws IOException {
        driver.navigate().to("https://copart.com");
        searchForCars("porsche");
        waitForSpinnerToClose();

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

    public void takeScreenshot(String filepath) throws IOException {
        TakesScreenshot screenshot = ((TakesScreenshot) driver);
        File image = screenshot.getScreenshotAs(OutputType.FILE);
        File destination = new File(filepath);
        FileUtils.copyFile(image, destination);
    }


    /**
     * Challenge 7
     */

    private void searchForCars(String car) {
        WebElement searchBarTbx = driver.findElement(By.id("input-search"));
        WebElement searchBtn = driver.findElement(By.xpath("//button[@data-uname='homepageHeadersearchsubmit']"));

        searchBarTbx.sendKeys(car);
        searchBtn.click();
    }

    private void waitForSpinnerToClose() {
        WebElement spinner = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='serverSideDataTable_processing']")));
        wait.until(ExpectedConditions.attributeToBe(spinner, "style", "display: block;"));
        wait.until(ExpectedConditions.attributeToBe(spinner, "style", "display: none;"));
    }

}
