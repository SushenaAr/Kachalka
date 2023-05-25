import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.WheelInput;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.Deserialisze;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ParserFromRadioRecord {
    public static HashMap<String, List<String>> getSoundsForArtists(WebDriver driver, List<String> artists) throws IOException, InterruptedException {
        HashMap<String, List<String>> arrayListHashMap = new HashMap<>();
        driver.get("https://www.radiorecord.ru/station/russiangold");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        Thread.sleep(1000L);
        WebElement ele = driver.findElements(By.className("_5KFtjamBMMZcRqzStezTlA==")).get(0);
        wait.until(ExpectedConditions.elementToBeClickable(ele));
        WheelInput.ScrollOrigin scrollOrigin = WheelInput.ScrollOrigin.fromElement(ele);
        new Actions(driver).scrollFromOrigin(scrollOrigin, 0, 700)
                .perform();
        for(int i = 0; i<3; i++){
            ele.click();
            Thread.sleep(500L);
            driver.findElements(By.className("xwFnNTTZe3O5HYtcMhmq1g==")).get(i).click();
            WebElement webElement = driver.findElements(By.className("_5KFtjamBMMZcRqzStezTlA==")).get(1);
            webElement.click();
            Thread.sleep(1000L);
            for (int item = 0; item < 6; item++) {
                WebElement webElement1 = driver.findElements(By.className("xwFnNTTZe3O5HYtcMhmq1g==")).get(item);
                try {
                    webElement1.click();
                } catch (ElementClickInterceptedException e) {
                    continue;
                }
                Thread.sleep(500L);
                try {
                    webElement.click();
                } catch (ElementClickInterceptedException e) {
                    webElement = driver.findElements(By.className("_5KFtjamBMMZcRqzStezTlA==")).get(1);
                }

                Thread.sleep(250L);
                try {
                    List<String> elemTitles = new Deserialisze(driver.findElements(By.className("HbATARj+9a-CWMaHl1J90A=="))).textFromWebElem;
                    List<String> elemAuthor = new Deserialisze(driver.findElements(By.className("RW+VBGHFGGn--xZDm6l57Q=="))).textFromWebElem;
                    for (int j = 0; j < elemTitles.size(); j++) {
                        String author = elemAuthor.get(j).split("/")[0];
                        String track = elemTitles.get(j).split("•")[0];
                        if (arrayListHashMap.containsKey(author)) {
                            if (arrayListHashMap.get(author).contains(track)) continue;
                            arrayListHashMap.get(author).add(track);
                        } else {
                            ArrayList<String> arrayList = new ArrayList<>();
                            arrayList.add(track);
                            arrayListHashMap.put(author, arrayList);
                        }
                    }
                } catch (StaleElementReferenceException e) {
                    List<String> elemTitles = new Deserialisze(driver.findElements(By.className("HbATARj+9a-CWMaHl1J90A=="))).textFromWebElem;
                    List<String> elemAuthor = new Deserialisze(driver.findElements(By.className("RW+VBGHFGGn--xZDm6l57Q=="))).textFromWebElem;
                    for (int j = 0; j < elemTitles.size(); j++) {
                        String author = elemAuthor.get(j).split("/")[0];
                        String track = elemTitles.get(j).split("•")[0];
                        if (arrayListHashMap.containsKey(author)) {
                            if (arrayListHashMap.get(author).contains(track)) continue;
                            arrayListHashMap.get(author).add(track);
                        } else {
                            ArrayList<String> arrayList = new ArrayList<>();
                            arrayList.add(track);
                            arrayListHashMap.put(author, arrayList);
                        }
                    }
                }
            }
        }

        System.out.println(arrayListHashMap);
        return arrayListHashMap;
    }
}
