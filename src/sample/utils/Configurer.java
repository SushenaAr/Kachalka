package utils;

import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.mitm.RootCertificateGenerator;
import net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverLogLevel;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

public class Configurer {

    private BrowserMobProxyServer webProxy;

    public Configurer() throws IOException {
        //setUpProxy();
    }

    public void stopProxy(){
        //webProxy.abort();
    }

    public static WebDriver setUpDriver(){
        WebDriver driver;
        ChromeOptions options = new ChromeOptions();
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/src/selenium/chromedriver.exe");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        return driver;
    }

    public void setUpProxy() {
        RootCertificateGenerator rootCertificateGenerator = RootCertificateGenerator.builder().build();
        rootCertificateGenerator.saveRootCertificateAndKey("PKCS12", new File(System.getProperty("user.dir") + "/tmp/keystore.p12"),
                "privateKeyAlias", "password");
        ImpersonatingMitmManager mitmManager = ImpersonatingMitmManager.builder()
                .rootCertificateSource(rootCertificateGenerator)
                .build();
        webProxy = new BrowserMobProxyServer();
        webProxy.setMitmManager(mitmManager);
        webProxy.start(0);
    }
}
