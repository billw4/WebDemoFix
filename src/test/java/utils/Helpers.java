package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Helpers {

    public Helpers() {
    }

    public void waitForSpinnerToClose(WebDriverWait wait) {
        WebElement spinner = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='serverSideDataTable_processing']")));
        wait.until(ExpectedConditions.attributeToBe(spinner, "style", "display: block;"));
        wait.until(ExpectedConditions.attributeToBe(spinner, "style", "display: none;"));
    }
}
