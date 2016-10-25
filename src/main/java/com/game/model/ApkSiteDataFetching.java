/*File Name		: ApkSiteDataFetching.java
 *Created By	: PRATIK RANJANE
 *Purpose		: Getting game information from APK-DL.com such as Game name, Version, Size,
 *				  Publish date, APK Download Link and storing it into CSV file.
 * */

package com.game.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.game.dto.JsonInfo;

public class ApkSiteDataFetching {

	private JsonInfo jsonInfo;

	public void setJsonInfo(JsonInfo jsonInfo) {
		this.jsonInfo = jsonInfo;
	}

	public ArrayList<String> createApkSiteDetails(String pack) {

		ArrayList<String> s1 = new ArrayList<String>();
		ArrayList<String> apkSiteDetails = new ArrayList<String>();

		String apk = "https://apk-dl.com/";
		String apkSite = apk.concat(pack);
		String downUrl = "";
		String title = "helo";
		try {
			// fetch the document over HTTP
			Document doc = Jsoup.connect(apkSite).userAgent("Chrome/47.0.2526.80").timeout(10000).get();

			// getting info class to fetch genre title version and publish date
			Elements infoClass = doc.select("[class=info section]");
			String in = infoClass.text();

			// getting data from info class
			String[] s3 = in.split("App Name</div>");
			for (String string : s3) {
				s1.add(string);
			}
			String info = s1.toString();

			// getting title
			title = info.substring((info.indexOf("Name") + 4), (info.indexOf("Package Name") - 1)).trim();

			// getting genre
			String genre = info.substring((info.indexOf("Category") + 8), (info.indexOf("Developer Visit") - 1)).trim();

			// getting version
			String version = info.substring((info.indexOf("Version") + 7), (info.indexOf("Developer") - 1)).trim();

			// getting publish date
			String pDate = info.substring((info.indexOf("Updated") + 7), (info.indexOf("File") - 1)).trim();

			// replacing comma in date with space
			pDate = pDate.replace(",", " ");

			// getting size
			String size = info.substring((info.indexOf("Size") + 4), (info.indexOf("Requires") - 1)).trim();

			// getting download link
			String downLink = doc.getElementsByClass("download-btn")
					.select("[class=mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect fixed-size mdl-button--primary]")
					.attr("href");

			// checking whether link contains "HTTP"
			if (downLink.contains("http") == false)
				downLink = ("http://apk-dl.com").concat(downLink.trim());

			// scraping downLink to get download link
			Document doc1 = Jsoup.connect(downLink).userAgent("Chrome/47.0.2526.80").timeout(10000).get();
			downUrl = doc1.getElementsByTag("p").select("a[href]").attr("href");

			if (downUrl != "") {
				// adding "HTTP" to link if absent
				if (downUrl.contains("http") == false) {
					downUrl = ("http:").concat(downUrl);
				}
			} else {
				// no download link present
				downUrl = downUrl.replaceAll(downUrl, "No download Link or paid app");
			}

			// if no data fetched
			if (title.equals("") && genre.equals("") && version.equals("") && size.equals("") && pDate.equals("")) {
				return null;
			} else {
				apkSiteDetails.add(title);
				apkSiteDetails.add(genre);
				apkSiteDetails.add(size);
				apkSiteDetails.add(version);
				apkSiteDetails.add(pDate);
				apkSiteDetails.add(downUrl);

				// displaying game info
				System.out.println("----------Dl-apk site data--------------");
				System.out.println("Title: " + title);
				System.out.println("Apk Site genre: " + genre);
				System.out.println("Version: " + version);
				System.out.println("Published Date: " + pDate);
				System.out.println("Size: " + size);
				System.out.println("Download Link:" + downUrl);
			}
		} catch (UnknownHostException u) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ApkSiteDataFetching as = new ApkSiteDataFetching();
			as.createApkSiteDetails(pack);
		} catch (Exception e) {

			return null;
		}
		return apkSiteDetails;
	}

	// Creating JSON file of fetched info
	public boolean createCsv(ArrayList<String> apkSiteDetails, String downloadFileName) {
		try {
			String title = apkSiteDetails.get(0);
			String genre = apkSiteDetails.get(1);
			String size = apkSiteDetails.get(2);
			String version = apkSiteDetails.get(3);
			String pDate = apkSiteDetails.get(4);
			String downUrl = apkSiteDetails.get(5);

			boolean notFound = false;

			File file = new File(jsonInfo.getCsvDownloadFilePath() + "/" + downloadFileName);

			if (!file.exists())
				notFound = true;

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);

			// if file doesn't exists, then create it
			if (notFound) {
				file.createNewFile();
				bw.append("PlayStore Title,Genre,Size,Version,Publish Date,Package,Url,");
				bw.append("Apk Title,Genre,Size,Version,Publish Date,Download Link,");
				bw.newLine();
			}
			// appending data to CSV
			bw.append(title);
			bw.append(",");
			bw.append(genre);
			bw.append(",");
			bw.append(size);
			bw.append(",");
			bw.append(version);
			bw.append(",");
			bw.append(pDate);
			bw.append(",");
			bw.append(downUrl);
			bw.append(",");
			if (downUrl.contains("http://dl3.apk-dl.com/store/download?id")) {
				System.out.println("inside if");
				bw.append("Broken Link");
			}
			bw.newLine();
			bw.close();
			System.out.println(title+" Apk-dl data Stored in csv");
			System.out.println("");
		}

		catch (Exception e) {
			return false;
		}
		return true;
	}

}
