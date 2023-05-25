import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;


public class DownloadFromZaycevNet{
    public static void runGetTracks(WebDriver driver, HashMap<String, List<String>> authorTracks) throws InterruptedException {
        for (Map.Entry<String, List<String>> entry : authorTracks.entrySet()) {
            for (String track : entry.getValue()) {
                String link = "https://zaycev.net/search?query_search=" + encodeURI(entry.getKey(), track);
                driver.get(link);
                List<WebElement> webElement = driver.findElements(By.className("yx7nr0-2"));
                if(!webElement.isEmpty()){
                    Boot.arrayList.add(track + " " + entry.getKey() + ";"+
                            driver
                                    .findElement(By.xpath("//*[@id=\"__next\"]/div[1]/main/div/section/div[2]/section[1]/ul/li[1]/div/div[1]/div/a[1]/div")).getText() + " " +
                            driver
                                    .findElement(By.xpath("//*[@id=\"__next\"]/div[1]/main/div/section/div[2]/section[1]/ul/li[1]/div/div[1]/div/a[2]/div")).getText() + ";" +
                            link);
                }
            }
        }
    }
    private static String encodeURI(String author, String str) {
        String finalURL = author + " " + str;
        return URLEncoder.encode(finalURL, StandardCharsets.UTF_8);
    }

    public static void downloadTheTrack(WebDriver driver, String url){
        driver.get(url);
        List<WebElement> webElement = driver.findElements(By.className("yx7nr0-2"));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.elementToBeClickable(webElement.get(0)));
        webElement.get(0).click();
    }
}
