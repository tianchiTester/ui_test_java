/*
 * Copyright (c) 2019, HP Development Company, L.P. All rights reserved.
 * This software contains confidential and proprietary information of HP.
 * The user of this software agrees not to disclose, disseminate or copy
 * such Confidential Information and shall use the software only in accordance
 * with the terms of the license agreement the user entered into with HP.
 */

package com.creditSuisse.ui.common;

import com.creditSuisse.utility.constants.Constants;
import com.creditSuisse.utility.ExceptionHandling;
import io.appium.java_client.windows.WindowsDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.github.bonigarcia.wdm.config.DriverManagerType.*;

/**
 * UI Automation driver-launcher for different platforms
 * (The specified driver must be configured in /resources/config/environment.properties)
 *
 * @author Tianchi Wu
 * @since 11/26/2019
 */
public class DriverFactory {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static DesiredCapabilities capabilities = new DesiredCapabilities();


    /**
     * driver configurer and initializer for specified platform
     *
     * @param platform name - e.i. ios, Android, Windows, Chrome (case does not matter)
     * @return WebDriver for specified platform
     * @throws ExceptionHandling if fails to get the driver
     * @throws IOException       if reading Chrome driver info fails
     */
    public static WebDriver initialDriver(String platform) throws IOException, ExceptionHandling {
        switch (platform.toLowerCase()) {
            case "windows":
                String appPackageName = UIEnvVariables.inst().getAppPackageName();
                if (appPackageName.toLowerCase().equals("root"))
                    capabilities.setCapability("app", appPackageName);
                else
                    capabilities.setCapability("app", appPackageName + "!App");
                capabilities.setCapability("platformName", "Windows");
                capabilities.setCapability("deviceName", "WindowsPC");
                driver.set(new WindowsDriver(new URL(UIEnvVariables.inst().getWindowsUrl()), capabilities));
                driver.get().manage().timeouts().implicitlyWait(Constants.Min_1, TimeUnit.SECONDS);
                break;
            case "android":
                break;
            case "ios":
                break;

            case "chrome":
                File omenApplicationDownloadedDirectory = new File(Constants.PATH_CHROME_DOWNLOAD);
                Map<String, Object> prefs = new HashMap<String, Object>();
                prefs.put("download.default_directory", omenApplicationDownloadedDirectory.getAbsolutePath());
                prefs.put("safebrowsing.enabled",true);
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--incognito");
                options.addArguments("start-maximized");
                options.setExperimentalOption("prefs", prefs);
                WebDriverManager.getInstance(CHROME).setup();
                driver.set(new ChromeDriver(options));
                break;
           /* System.setProperty("webdriver.chrome.driver","C:\\Users\\Nabarupa_Das\\Downloads\\chromedriver.exe");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--incognito");
            DesiredCapabilities capabilities = DesiredCapabilities.chrome();
            capabilities.setCapability(ChromeOptions.CAPABILITY, options);
            WebDriver driver=new ChromeDriver(capabilities);*/


            case "ie":
                capabilities = DesiredCapabilities.internetExplorer();
                capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                capabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, false);
                capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
                capabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, false);
                capabilities.setBrowserName(DesiredCapabilities.internetExplorer().getBrowserName());
                WebDriverManager.getInstance(IEXPLORER).driverRepositoryUrl(new URL("http://selenium-release.storage.googleapis.com/")).setup();
                driver.set(new InternetExplorerDriver(new InternetExplorerOptions(capabilities)));
                break;
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                FirefoxProfile profile = new FirefoxProfile();
                profile.setPreference("security.ssl3.dhe_rsa_aes_128_sha", false);
                profile.setPreference("security.ssl3.dhe_rsa_aes_256_sha", false);
                profile.setAcceptUntrustedCertificates(true);
                profile.setPreference("acceptSslCerts", true);
                profile.setAssumeUntrustedCertificateIssuer(false);
                profile.setPreference("browser.download.folderList", 2);
                profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "text/csv, application/pdf, application/msword, application/octet-stream");
                firefoxOptions.setProfile(profile);
                capabilities = DesiredCapabilities.firefox();
                capabilities.setCapability("marionette", true);
                capabilities.setBrowserName("firefox");//DesiredCapabilities.firefox().getBrowserName()
                capabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, firefoxOptions);
                WebDriverManager.getInstance(FIREFOX).useMirror().setup();
                driver.set(new FirefoxDriver(new FirefoxOptions(capabilities)));
                break;
        }
        return driver.get();
    }

    /**
     * returns the capabilities of a driver
     *
     * @return capability object
     */
    public static DesiredCapabilities getCapabilities() {
        return capabilities;
    }

    /**
     * winAppDriver launcher for Windows automation
     * (dependencies - WinAppDriver must be installed on local system and Constant-path configured)
     */
    public static void startWinAppDriver() {
        try {
            Runtime.getRuntime().exec(Constants.PATH_CMD + " /c start /min WinAppDriver.exe", null, new File(Constants.PATH_WIN_APP_DRIVER));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
