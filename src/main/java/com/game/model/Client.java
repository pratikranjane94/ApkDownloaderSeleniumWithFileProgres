package com.game.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import org.json.simple.parser.ParseException;
import org.openqa.selenium.chrome.ChromeDriver;

public class Client {

	static int last = 0;

	public static void main(String[] args) throws InterruptedException, IOException, ParseException {
		Scanner scanner = new Scanner(System.in);

		ApkDownloadSelenium apkDownloadSelenium = new ApkDownloadSelenium();
		IsDownloaded isDownloaded = new IsDownloaded();

		ArrayList<String> urlList = new ArrayList<>();
		ArrayList<ChromeDriver> driverList = new ArrayList<>();

		String csvFilePath = "/home/bridgelabz6/Downloads/eclipse/as.csv";
		String downloadFilePath = "/home/bridgelabz6/Pictures/files/";

		int size = 0;

		System.out.println("Enter csv file path:");
		// csvFilePath = scanner.next();

		System.out.println("Enter path where you want to store the download file:");
		// downloadFilePath = scanner.next();

		// -------------------- rest call to upload
		// file------------------------------

		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(
				"http://localhost:8080/ApkDownloaderSeleniumWithFileProgres/rest/controller/upload");

		File file = new File(csvFilePath);
		FileBody fileBody = new FileBody(file);

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

		// builder.setContentType(ContentType.MULTIPART_FORM_DATA);
		builder.addPart("files", fileBody);
		HttpEntity entity = builder.build();
		httpPost.setEntity(entity);

		// execute HTTP post request
		HttpResponse response = httpClient.execute(httpPost);
		System.out.println("rsponse from :" + response.toString());

		// ------------------end of rest call----------------------

		// reading file name from response

		String fileName = response.toString();
		fileName = fileName.substring(fileName.indexOf("filename=") + 10, fileName.indexOf(", Content-Type") - 1);
		System.out.println("file name:" + fileName);

		// end of reading file name

		// -----------------downloading file to client machine------------

		HttpEntity resEntity = response.getEntity();

		if (resEntity != null) {

			BufferedInputStream bis = new BufferedInputStream(resEntity.getContent());

			String filePath = downloadFilePath + "/" + fileName;

			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
			int inByte;

			while ((inByte = bis.read()) != -1)
				bos.write(inByte);
			bis.close();
			bos.close();

			// ---------------------end of downloading file-------------------

			System.out.println("reading file");

			// getting list of play store link by reading file
			urlList = apkDownloadSelenium.readFile(filePath);

			// -------opening play store links using selenium--------
			size = urlList.size() / 5;
			size = size * 5;
			for (int i = 0; i < size; i++) {
				System.out.println("play store url:" + urlList.get(i));

				// downloading APK using selenium
				driverList.add(apkDownloadSelenium.downloadApkUsingSelenium(urlList.get(i)));

				if (i % 5 == 4) {
					System.out.println("checking after 5 k: " + 5 + " i: " + i);
					isDownloaded.isDownloadCompleted(fileName, i);
					for (ChromeDriver driver : driverList) {
						apkDownloadSelenium.closeTabs(driver);
					}
					driverList.clear();
				}

			}
			for (int j = size; j < urlList.size(); j++) {
				System.out.println("last for loop started");
				driverList.add(apkDownloadSelenium.downloadApkUsingSelenium(urlList.get(j)));
			}
			System.out.println("size:" + size + " Urlist size: " + urlList.size());
			// checking download completed or not
			isDownloaded.isDownloadCompleted(fileName, urlList.size());

			// closing all tabs

			for (ChromeDriver driver : driverList) {
				apkDownloadSelenium.closeTabs(driver);
			}

			System.out.println("done");

		}
		scanner.close();
	}// end of rest call
}
