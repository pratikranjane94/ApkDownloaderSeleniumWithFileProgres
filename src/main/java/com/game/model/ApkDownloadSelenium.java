/*File Name		: ApkDownloadSelenium.java
 *Created By	: PRATIK RANJANE
 *Purpose		: 
 * */

package com.game.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;


import com.game.dto.JsonInfo;

public class ApkDownloadSelenium {

	private JsonInfo jsonInfo;

	public void setJsonInfo(JsonInfo jsonInfo) {
		this.jsonInfo = jsonInfo;
	}

	public ChromeDriver downloadApkUsingSelenium(String playStoreUrl)
			throws InterruptedException, IOException, ParseException {

		ChromeDriver driver = null;
		String username = null;
		String password = null;
		String androidId = null;

		// getting credentials

		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(jsonInfo.getCredentialsPath()));

			JSONObject jsonObject = (JSONObject) obj;

			username = (String) jsonObject.get("username");

			password = (String) jsonObject.get("password");

			androidId = (String) jsonObject.get("androidId");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// end of getting credentials

		try {
			// login URL
			String chromeUrl = "chrome-extension://bifidglkmlbfohchohkkpdkjokajibgg/login.html";

			System.setProperty("webdriver.chrome.driver", jsonInfo.getChromeDriverPath());

			ChromeOptions options = new ChromeOptions();

			// adding extension in CHROME
			options.addExtensions(new File(jsonInfo.getChromeExtensionPath()));

			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);
			driver = new ChromeDriver(capabilities);

			System.out.println("Opening page:" + playStoreUrl);

			// opening login page
			driver.get(chromeUrl);

			// setting email id
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
			driver.findElement(By.id("inp-email")).sendKeys(username);
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

			// setting password
			driver.findElement(By.id("inp-password")).sendKeys(password);
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

			// setting android id
			driver.findElement(By.id("inp-gsf-id")).sendKeys(androidId);
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

			// clicking login button
			driver.findElement(By.id("inp-gsf-id")).sendKeys(Keys.ENTER);
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

			// opening play store tab in new window
			String selectLinkOpeninNewTab = Keys.chord(Keys.CONTROL, "t");
			driver.findElement(By.id("inp-gsf-id")).sendKeys(selectLinkOpeninNewTab);

			ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());

			// switches to new tab
			driver.switchTo().window(tabs.get(1));
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

			// opening game's play store page
			driver.get(playStoreUrl);
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

			// clicking download button
			driver.findElement(By.cssSelector("span.large.play-button.download-apk-button.apps button")).click();
			// Maximize the window.

			// driver.manage().window().maximize();
			System.out.println("Automation Completed");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("selenium exception");
			downloadApkUsingSelenium(playStoreUrl);
		}

		return driver;
	}

	// logging out and closing all CHROME windows
	public void closeTabs(ChromeDriver driver) {

		try {
			// listing all tabs
			ArrayList<String> tabs2 = new ArrayList<String>(driver.getWindowHandles());
			driver.switchTo().window(tabs2.get(1));
			driver.close();

			driver.switchTo().window(tabs2.get(2));
			driver.close();

			driver.switchTo().window(tabs2.get(3));
			driver.close();

			driver.switchTo().window(tabs2.get(0));

			WebElement element = driver.findElement(By.id("btn-logout"));
			JavascriptExecutor executor = (JavascriptExecutor) driver;
			executor.executeScript("arguments[0].click();", element);
			driver.switchTo().alert().accept();
			driver.quit();
		} catch (Exception e) {
			System.err.println("Exception in closing tab");

		}

	}

	// reading download file to get play store URL
	public ArrayList<String> readFile(String filePath) throws IOException {

		String url = null;
		// String line;
		int progress;
		int count = 0;
		int totoalGames = 0;

		ArrayList<String> urlList = new ArrayList<>();
		// counting no of games in file
		FileReader frCount = new FileReader(filePath);
		BufferedReader brCount = new BufferedReader(frCount);
		while (brCount.readLine() != null) {
			count++;
		}
		totoalGames = count - 1;
		brCount.close();
		// end of counting no of games

		// reading game name
		FileReader fr = new FileReader(filePath);
		BufferedReader br = new BufferedReader(fr);
		url = br.readLine();
		if (url == null) {
			System.out.println("file is empty");
		} else {
			// line = br.readLine();
			for (progress = 0; progress < totoalGames; progress++) {
				url = br.readLine();
				String[] gname = url.split("\\,");
				try {
					url = gname[6];
				} catch (Exception e) {
					System.err.println("unable to read url");
				}
				// System.out.println("playstore url:"+url);
				if (!url.equals(null))
					urlList.add(url);
			}
			url = br.readLine();
		}
		br.close();
		return urlList;
	}

}
