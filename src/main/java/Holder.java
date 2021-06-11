import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Holder {
    public WebDriver driver;
    public WebDriverWait wait;

    @BeforeSuite
    public void startSuite() throws Exception {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/drivers/chromedriver.exe");
        driver =  new ChromeDriver();
//        driver.manage().timeouts().implicitlyWait(10, SECONDS);
        wait = new WebDriverWait(driver, 10);
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

    @Ignore
    @Test()
    public void verifyGoogleTitle() throws Exception{
        Assert.assertEquals(driver.getTitle(), "Google");
    }

    @Test (priority = 1)
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

    @Test (priority = 2)
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

    @Test (priority = 3)
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

    @Test (priority = 4)
    public void takeScreenshotOnException() throws IOException {
        driver.navigate().to("https://copart.com");
        searchForCars("nissan");
        waitForSpinnerToClose();

        WebElement modelFilterBtn = driver.findElement(By.xpath("//a[@data-uname='ModelFilter']"));
        modelFilterBtn.click();

        WebElement searchModelFilterTbx = driver.findElement(By.xpath("//a[@data-uname='ModelFilter']/ancestor::li//form//input"));
        searchModelFilterTbx.sendKeys("skylinx");

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

    @Test (priority = 5)
    public void create2DimensionalArraysAndAssertion() {
        driver.navigate().to("https://copart.com");
        List<WebElement> makes = driver.findElements(By.xpath("//span[@class='make-items']//a"));

        int modelCount = makes.size();
//        String[][] makeUrls = new String[modelCount][2];
//
//        for (int i = 0; i< makeUrls.length; i++) {
//            if (!makes.get(i).getText().equals("More...")) {
//                makeUrls[i][0] = makes.get(i).getText();
//                makeUrls[i][1] = makes.get(i).getAttribute("href");
//            }
//        }

//        for (int row = 0; row < makeUrls.length; row++) {
//            if (makeUrls[row][0]!=null) {
//                System.out.println("Make: " + makeUrls[row][0]);
//                System.out.println("URL: " + makeUrls[row][1]);
//                driver.navigate().to(makeUrls[row][1]);
//                Assert.assertTrue(driver.getCurrentUrl().contains(makeUrls[row][0].replaceAll(" ", "%20")),
//                        "URL for Make '" + makeUrls[row][0] + "' is invalid.");
//            }
//        }

//        for (String[] makeUrl : makeUrls) {
//            System.out.println("Make: " + makeUrl[0]);
//            System.out.println("URL: " + makeUrl[1]);
//        }

        Map<String, String> makeUrls = new HashMap<>();
        for (WebElement element : makes) {
            makeUrls.put(element.getText(), element.getAttribute("href"));
        }

        for (Map.Entry<String, String> entry : makeUrls.entrySet()) {
            System.out.println("Make: " + entry.getKey() + ", URL: " + entry.getValue());
            driver.navigate().to(entry.getValue());
            if (!entry.getKey().equals("More...") ) {
                Assert.assertTrue(driver.getCurrentUrl().contains(entry.getKey().replaceAll(" ", "%20")),
                    "URL for Make '" + entry.getKey() + "' is invalid.");
            }
        }
    }

    @Test
    public void RestWebServiceTest() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String query = "toyota camry";
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("query", query)
                .build();

        Request request = new Request.Builder()
                .url("https://www.copart.com/public/lots/search")
                .post(body)
                .build();

        ResponseBody responseBody = client.newCall(request).execute().body();
        String json = responseBody.string();

        JsonElement root = JsonParser.parseString(json);
        JsonObject dataObj = root.getAsJsonObject();
        JsonElement dataElem = dataObj.get("data");
        JsonElement resultsElem = dataElem.getAsJsonObject().get("results");
        JsonElement total = resultsElem.getAsJsonObject().get("totalElements");

        String filePath = "src/main/resources/files/output.txt";
        writeTextToFile(filePath,  query + ": " + total);

        List<String> modelSearches = new ArrayList<>();
        modelSearches.add("volkswagon beetle");
        modelSearches.add("tesla s3");
        modelSearches.add("ford mustang");
        modelSearches.add("dodge charger");
        modelSearches.add("porche 911");

        for (String model : modelSearches) {
            String makeTotal = getBackResponseTotal(query);
            writeTextToFile(filePath, model + ": " + makeTotal);
        }
    }

    @Test
    public void diceJobCount() {
        driver.navigate().to("https://www.dice.com/");

        WebElement searchTbx = driver.findElement(By.xpath("//input[@placeholder='Job title, skills or company']"));
        WebElement searchLocation = driver.findElement(By.xpath("//input[@id='google-location-search']"));
        WebElement searchBtn = driver.findElement(By.xpath("//button[@id='submitSearch-button']"));

        searchTbx.sendKeys("QA Engineer");
        searchLocation.sendKeys("Utah");
        searchBtn.click();


        WebElement table = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//js-search-display//div[@id='searchDisplay-div']")));
        wait.until(ExpectedConditions.visibilityOf(table));

        List<WebElement> companies = driver.findElements(By.xpath("//js-search-display//div/a"));

        List<String> resultUrls = new ArrayList<>();

        System.out.println("List size: " + companies.size());

        for (WebElement company : companies) {
            System.out.println(company.getText());
            resultUrls.add(company.getAttribute("href"));
        }

        for (String url : resultUrls) {
            driver.navigate().to(url);
            WebElement company = driver.findElement(By.xpath("//h1[@class='company-name']"));
            List<WebElement> jobs = driver.findElements(By.xpath("//div[@id='cserp']//a"));
            int jobCount = jobs.size();
            if (jobCount==20) {
                System.out.println(company.getText() + " has " + jobCount + " or more jobs available.");
            } else {
                System.out.println(company.getText() + " has " + jobCount + " jobs available.");
            }
        }

    }

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

    public void takeScreenshot(String filepath) throws IOException {
        TakesScreenshot screenshot = ((TakesScreenshot) driver);
        File image = screenshot.getScreenshotAs(OutputType.FILE);
        File destination = new File(filepath);
        FileUtils.copyFile(image, destination);
    }

    public void writeTextToFile(String filePath, String text) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
        writer.append(text);
        writer.append("\n");
        writer.close();
    }

    public String getBackResponseTotal(String query) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("query", query)
                .build();

        Request request = new Request.Builder()
                .url("https://www.copart.com/public/lots/search")
                .post(body)
                .build();

        ResponseBody responseBody = client.newCall(request).execute().body();
        String json = responseBody.string();

        JsonElement root = JsonParser.parseString(json);
        JsonObject dataObj = root.getAsJsonObject();
        JsonElement dataElem = dataObj.get("data");
        JsonElement resultsElem = dataElem.getAsJsonObject().get("results");
        JsonElement total = resultsElem.getAsJsonObject().get("totalElements");

        return String.valueOf(total);
    }
}
