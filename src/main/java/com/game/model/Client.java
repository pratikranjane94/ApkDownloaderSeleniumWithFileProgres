/*File Name		: Client.java
 *Created By	: PRATIK RANJANE
 *Purpose		: Entry point of project, sends file to the server,download APK using SELENIUM and checks whether APK are download or not
 * */

package com.game.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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

public class Client {

	public static void main(String[] args) throws InterruptedException, IOException, ParseException {

		ApkDownloadSelenium apkDownloadSelenium = new ApkDownloadSelenium();
		IsDownloaded isDownloaded = new IsDownloaded();
		JsonInfo jsonInfo = new JsonInfo();

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
		String downloadFilePath = "";
		String propertyJsonPath = "";
		String fileName;
		String filePath;
		
		int size = 0;

		System.out.println("Enter property.json file path:");
		 propertyJsonPath = scanner.next();

		try {

			Object obj = parser.parse(new FileReader(propertyJsonPath));

			JSONObject jsonObject = (JSONObject) obj;

			// getting properties from JSON
			jsonInfo.setRestCall((String) jsonObject.get("restCall"));
			jsonInfo.setChromeDriverPath((String) jsonObject.get("chromeDriverPath"));
			jsonInfo.setCredentialsPath((String) jsonObject.get("credentialsPath"));
			jsonInfo.setChromeExtensionPath((String) jsonObject.get("chromeExtensionPath"));
			jsonInfo.setApkFileDownloadFolder((String) jsonObject.get("apkFileDownloadFolder"));
			System.out.println("restCall:" + jsonInfo.getRestCall() + "\ndriver path:" + jsonInfo.getChromeDriverPath()
					+ "\n cred path:" + jsonInfo.getCredentialsPath());
			System.out.println("extension path:" + jsonInfo.getChromeExtensionPath() + "\n download folder"
					+ jsonInfo.getApkFileDownloadFolder());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// passing properties from JSON to respective class
		apkDownloadSelenium.setJsonInfo(jsonInfo);
		isDownloaded.setJsonInfo(jsonInfo);

		System.out.println("Enter csv file path:");
		 csvFilePath = scanner.next();

		System.out.println("Enter path where you want to store the download file:");
		 downloadFilePath = scanner.next();		

		// --------------- rest call to upload file-----------------

		HttpClient httpClient = HttpClientBuilder.create().build();

		// calling REST
		HttpPost httpPost = new HttpPost(jsonInfo.getRestCall());

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
		System.out.println("response filename:"+fileName);
		if(jsonInfo.getRestCall().contains("localhost"))
			fileName = fileName.substring(fileName.indexOf("filename=") + 10, fileName.indexOf(", Content-Type") - 1);
		else
			fileName = fileName.substring(fileName.indexOf("filename=") + 10, fileName.indexOf(", Server:") - 1);
		System.out.println("file name:" + fileName);

		// end of reading file name

		// -----------------downloading file to client machine------------

		resEntity = response.getEntity();

		if (resEntity != null) {

			bis = new BufferedInputStream(resEntity.getContent());
			
			filePath = downloadFilePath + "/" + fileName;

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

			System.out.println("url list:"+urlList.toString());
			
			// -------opening play store links using selenium--------
			size = urlList.size() / 5;
			size = size * 5;
			for (int i = 0; i < size; i++) {
				System.out.println("play store url:" + urlList.get(i));

				// downloading APK using selenium
				driverList.add(apkDownloadSelenium.downloadApkUsingSelenium(urlList.get(i)));

				if (i % 5 == 4) {
					System.out.println("checking after 5 k: " + 5 + " i: " + i);
					isDownloaded.isDownloadCompleted(downloadFilePath, fileName, i);
					for (ChromeDriver driver : driverList) {
						apkDownloadSelenium.closeTabs(driver);
					}
					driverList.clear();
				}

			}
			if (urlList.size() != size) {
				for (int j = size; j < urlList.size(); j++) {
					System.out.println("last for loop started");
					driverList.add(apkDownloadSelenium.downloadApkUsingSelenium(urlList.get(j)));
				}
				System.out.println("size:" + size + " Urlist size: " + urlList.size());
				// checking download completed or not
				isDownloaded.isDownloadCompleted(downloadFilePath, fileName, urlList.size());
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
