/*File Name	: FileController.java
 *Created By: PRATIK RANJANE
 *Purpose	: Storing the uploaded file, creating JOUSP of games presents in files,
 *			  creating CSV file of details of games using and displaying on web page using Socket,
 *			  Downloading the file. 
 * */
package com.game.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.openqa.selenium.chrome.ChromeDriver;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.game.dao.GameJsoupDaoImp;
import com.game.dto.FileMeta;
import com.game.model.ApkDownloadSelenium;
import com.game.model.ApkSiteDataFetching;
import com.game.model.GameNotFound;
import com.game.model.PlayStoreDataFetching;
import com.game.model.PlayStoreUrlFetching;

@RestController
@EnableWebMvc
@RequestMapping("/controller")
public class FileController {


	@Resource(name = "gameJsoupDao")
	private GameJsoupDaoImp gameJsoupDao;

	/*-------------------------------------------Creating JSOUP of Uploaded File-------------------------------------------*/

	@SuppressWarnings("unused")
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public void upload(@RequestParam("files") MultipartFile mpf, HttpServletResponse response)
			throws IOException, InterruptedException {
		
		// Class objects are created
		PlayStoreUrlFetching playStoreUrlFetching = new PlayStoreUrlFetching();
		PlayStoreDataFetching playStoreDataFetching = new PlayStoreDataFetching();
		ApkSiteDataFetching apkSiteDataFetching = new ApkSiteDataFetching();
		GameNotFound gameNotFound = new GameNotFound();
		ApkDownloadSelenium apkDownloadSelenium = new ApkDownloadSelenium();
		FileMeta fileMeta = new FileMeta();

		ArrayList<String> playStoreDetails = new ArrayList<String>();
		ArrayList<String> apkSiteDetails = new ArrayList<String>();
		ArrayList<ChromeDriver> closeTabs = new ArrayList<>();

		String url = ""; // Play Store URL
		String line; // line read from file and stores game name
		String temp = ""; // game name temporary
		String fileName; // name of uploaded file
		String downloadFileName; // downloading name for file.
		String fileNameID = null; //file name with id
		int progress = 0; // no of game's data is scraped
		int noOfLines = 0; // total no of game in file
		int id=0; // unique id for each uploaded file
		int totalGames = 0; // total games in file
		boolean status = true; // status of APK-DL CSV created or not
		boolean psStatus = true; // status of PlayStore CSV created or not

		System.out.println("-----------------* Game Scraping Controller *-----------------");

		// getting uploaded MULTIPART file and its information
			System.out.println(mpf.getOriginalFilename() + " uploaded! ");

			// getting original filename and setting download filename
			fileName = mpf.getOriginalFilename();
			downloadFileName = mpf.getOriginalFilename().replace(".", "Download.");

			try {
				// storing data in file meta class
				fileMeta.setFileName(fileName);
				fileMeta.setFileType(mpf.getContentType());
				fileMeta.setBytes(mpf.getBytes());
				
				// getting data from uploaded file
				InputStreamReader reader=new InputStreamReader(mpf.getInputStream());
				InputStreamReader reader2=new InputStreamReader(mpf.getInputStream());

				BufferedReader brCount = new BufferedReader(reader);
				
				// counting no of games in file
				while (brCount.readLine() != null) {
					noOfLines++;
				}
				totalGames = noOfLines - 1;

				// reseting count to zero
				noOfLines = 0;

				fileMeta.setTotalGames(totalGames);
				System.out.println("totalGames:" + totalGames);

				// end of counting game

				
				/*----------------------------Scraping started--------------------------------*/

				BufferedReader br = new BufferedReader(reader2);

				line = br.readLine();
				if (line == null) {
					System.out.println("file is empty");
				} else {
					line = br.readLine();

					for (progress = 0; progress < totalGames; progress++) {

						fileMeta.setProgress(progress);

						temp = line;
						String[] gname = line.split("\\,");

						// Separates the game name from line read from file
						line = gname[1];
						System.out.println("Game Name= " + line);

						// getting URL for game
						url = playStoreUrlFetching.findUrl(line);

						line = br.readLine();

						// exception handling if URL not found
						if (url == null) {
							System.err.println("URL Not Found");
							continue;
						} // end of handling in URL fetching

						// getting play store site data
						playStoreDetails = playStoreDataFetching.getPlayStoreData(url);

						// handling exception in play store details
						if (playStoreDetails.equals(null)) {
							System.err.println("PlayStore Data Not Found");
						} // end of handling in play store details

						// getting play store package name
						String pack = playStoreDataFetching.getPackage(playStoreDetails);

						// getting APK-DL site data
						apkSiteDetails = apkSiteDataFetching.createApkSiteDetails(pack);

						// handling exception in APK site details
						if (apkSiteDetails == null) {
							System.err.println("DL-APK Data Not Found");
						} // end of handling in APK-DL

						// Database entry
						if (gameJsoupDao.isEmpty())
							id = 0;
						else {
							// if last filename is same as current filename
							// assign same id to game data
							if (gameJsoupDao.checkLastFileName().equals(fileName))
								id = gameJsoupDao.checkId(fileName);
							else {
								 /*if filename not matched, different(i.e new)
								 file is uploaded assign new id(i.e increase
								 id)*/
								id = gameJsoupDao.checkLastId();
								id = id + 1;
							}
						}

						// data is inserted into database
						gameJsoupDao.insert(id, progress + 1, fileName, playStoreDetails, apkSiteDetails);

						// end of database entry

						System.out.println("For progress:" + progress);

					} // end of for

					fileNameID = fileName.replace(".", Integer.toString(id) + ".");
					gameJsoupDao.update(fileNameID, id);
					fileMeta.setFileName(fileNameID);
				} // end of else
				br.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("-----------End Of Program-----------");

		// end of scraping function

		// sending file data in response

		try {

			System.out.println("after download file name:" + fileNameID);

			// getting file data from database
			String data =gameJsoupDao.getFileRecords(fileNameID);

			response.setContentType(fileMeta.getFileType());
			response.setHeader("Content-disposition","attachment; filename=\"" + fileMeta.getFileName() + "\"");
			
			FileCopyUtils.copy(data.getBytes(), response.getOutputStream());
			
			System.out.println("Name of file to be downloaded:" + fileMeta.getFileName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}// End of sending file data in response

}// End of class




