/*File Name		: Client.java
 *Created By	: PRATIK RANJANE
 *Purpose		: Entry point of project, sends file to the server,download APK using SELENIUM 
 *				  and checks whether APK are download or not
 * */

package com.game.Client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.openqa.selenium.chrome.ChromeDriver;

import com.game.dto.JsonInfo;
import com.game.model.ApkDownloadSelenium;
import com.game.model.ApkSiteDataFetching;
import com.game.model.IsDownloaded;
import com.game.model.PlayStoreDataFetching;

public class Client {

	public static void main(String[] args) throws InterruptedException, IOException, ParseException {
		ApkDownloadSelenium apkDownloadSelenium = new ApkDownloadSelenium();
		IsDownloaded isDownloaded = new IsDownloaded();
		JsonInfo jsonInfo = new JsonInfo();
		PlayStoreDataFetching playStoreDataFetching = new PlayStoreDataFetching();
		ApkSiteDataFetching apkSiteDataFetching = new ApkSiteDataFetching();

		JSONParser parser = new JSONParser();
		Scanner scanner = new Scanner(System.in);

		ArrayList<String> urlList = new ArrayList<>();
		ArrayList<ChromeDriver> driverList = new ArrayList<>();

		File file;
		FileBody fileBody;
		MultipartEntityBuilder builder;
		HttpResponse response;
		HttpEntity entity;
		HttpEntity resEntity;

		BufferedInputStream bis;
		BufferedOutputStream bos;

		String csvFilePath = "";

		String propertyJsonPath = "";
		String fileName;
		String filePath;

		int size = 0;

		System.out.println("Enter property.json file path:");
		propertyJsonPath = scanner.next();

		Object obj = parser.parse(new FileReader(propertyJsonPath));

		JSONObject jsonObject = (JSONObject) obj;

		// getting properties from JSON
		jsonInfo.setRestCall((String) jsonObject.get("restCall"));
		jsonInfo.setChromeDriverPath((String) jsonObject.get("chromeDriverPath"));
		jsonInfo.setCredentialsPath((String) jsonObject.get("credentialsPath"));
		jsonInfo.setChromeExtensionPath((String) jsonObject.get("chromeExtensionPath"));
		jsonInfo.setApkFileDownloadFolder((String) jsonObject.get("apkFileDownloadFolder") + "/");
		jsonInfo.setCsvDownloadFilePath((String) jsonObject.get("csvDownloadFilePath"));
		System.out.println("RestCall:" + jsonInfo.getRestCall() + "\nChrome driver path:"
				+ jsonInfo.getChromeDriverPath() + "\nLogin Credentials path:" + jsonInfo.getCredentialsPath()
				+ "\nChrome extension path:" + jsonInfo.getChromeExtensionPath() + "\nApk Download folder"
				+ jsonInfo.getApkFileDownloadFolder());

		System.out.println("Enter csv file path:");
		csvFilePath = scanner.next();

		// passing properties from JSON to respective class
		apkDownloadSelenium.setJsonInfo(jsonInfo);
		isDownloaded.setJsonInfo(jsonInfo);
		playStoreDataFetching.setJsonInfo(jsonInfo);
		apkSiteDataFetching.setJsonInfo(jsonInfo);

		// --------------- rest call to upload file-----------------

		HttpClient httpClient = HttpClientBuilder.create().build();

		// calling REST
		HttpPost httpPost = new HttpPost(jsonInfo.getRestCall());

		// attaching file with request
		file = new File(csvFilePath);
		fileBody = new FileBody(file);

		builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

		// builder.setContentType(ContentType.MULTIPART_FORM_DATA);
		builder.addPart("files", fileBody);
		entity = builder.build();
		httpPost.setEntity(entity);

		// execute HTTP post request
		response = httpClient.execute(httpPost);
		System.out.println("rsponse from :" + response.toString());

		// ------------------end of rest call----------------------

		// reading file name from response

		fileName = response.toString();
		System.out.println("response filename:" + fileName);
		if (jsonInfo.getRestCall().contains("localhost"))
			fileName = fileName.substring(fileName.indexOf("filename=") + 10, fileName.indexOf(", Content-Type") - 1);
		else
			fileName = fileName.substring(fileName.indexOf("filename=") + 10, fileName.indexOf(", Server:") - 1);
		System.out.println("file name:" + fileName);

		// end of reading file name

		// -----------------downloading file to client machine------------

		resEntity = response.getEntity();

		if (resEntity != null) {

			bis = new BufferedInputStream(resEntity.getContent());

			filePath = jsonInfo.getCsvDownloadFilePath() + "/" + fileName;

			bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
			int inByte;

			while ((inByte = bis.read()) != -1)
				bos.write(inByte);
			bis.close();
			bos.close();

			// ---------------------end of downloading file-------------------

			System.out.println("reading file");

			// getting list of play store link by reading file
			urlList = apkDownloadSelenium.readFile(filePath);

			System.out.println("url list:" + urlList.toString());

			// -------opening play store links using SELENIUM--------
			size = urlList.size() / 5;
			size = size * 5;
			for (int i = 0; i < size; i++) {
				System.out.println("play store url:" + urlList.get(i));

				// downloading APK using SELENIUM
				driverList.add(apkDownloadSelenium.downloadApkUsingSelenium(urlList.get(i)));

				if (i % 5 == 4) {
					// System.out.println("checking after 5 k: " + 5 + " i: " +
					// i);
					// checking download completed or not
					isDownloaded.isDownloadCompleted(jsonInfo.getCsvDownloadFilePath(), fileName, i);

					// closing all tabs
					for (ChromeDriver driver : driverList) {
						apkDownloadSelenium.closeTabs(driver);
					}
					driverList.clear();
				}

			}

			if (urlList.size() != size) {
				for (int j = size; j < urlList.size(); j++) {
					driverList.add(apkDownloadSelenium.downloadApkUsingSelenium(urlList.get(j)));
				}
				System.out.println("size:" + size + " Urlist size: " + urlList.size());
				// checking download completed or not
				isDownloaded.isDownloadCompleted(jsonInfo.getCsvDownloadFilePath(), fileName, urlList.size());
			}
			// closing all tabs
			for (ChromeDriver driver : driverList) {
				apkDownloadSelenium.closeTabs(driver);
			}

			System.out.println("done");

		}
		scanner.close();
	}// end of rest call
}
