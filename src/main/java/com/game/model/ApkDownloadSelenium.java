package com.game.model;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

public class ApkDownloadSelenium {
	public void downloadApkUsingSelenium(String playStoreUrl) throws InterruptedException {
		System.setProperty("webdriver.chrome.driver", "/home/bridgelabz6/Downloads/chromedriver");

		ChromeOptions options = new ChromeOptions();
		options.addExtensions(new File("/home/bridgelabz6/Music/directapk.crx"));

		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		ChromeDriver driver = new ChromeDriver(capabilities);
		System.out.println("Opening page");

		String chromeUrl = "chrome-extension://bifidglkmlbfohchohkkpdkjokajibgg/login.html";

		driver.get(chromeUrl);

		// setting email id
		driver.findElement(By.id("inp-email")).sendKeys("apkdownloadtesting@gmail.com");
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		// setting password
		driver.findElement(By.id("inp-password")).sendKeys("TEsting@123");
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		// setting android id
		driver.findElement(By.id("inp-gsf-id")).sendKeys("3B3BA0D7BE8C2194");
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

		// clicking login button
		driver.findElement(By.id("inp-gsf-id")).sendKeys(Keys.ENTER);

		String selectLinkOpeninNewTab = Keys.chord(Keys.CONTROL, "t");
		driver.findElement(By.id("inp-gsf-id")).sendKeys(selectLinkOpeninNewTab);
		ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(tabs.get(1)); // switches to new tab
		driver.get(playStoreUrl);

		// clicking download button
		driver.findElement(By.cssSelector("span.large.play-button.download-apk-button.apps button")).click();
		// Maximize the window.
		driver.manage().window().maximize();
		System.out.println("Download Complete");

		driver.close();
	}

}
