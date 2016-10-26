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
import java.util.Iterator;
import java.util.LinkedList;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.openqa.selenium.chrome.ChromeDriver;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
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
	public void upload(MultipartHttpServletRequest request, HttpServletResponse response)
			throws IOException, InterruptedException {
		
		// Class objects are created
		PlayStoreUrlFetching purl = new PlayStoreUrlFetching();
		PlayStoreDataFetching psdf = new PlayStoreDataFetching();
		ApkSiteDataFetching asdf = new ApkSiteDataFetching();
		GameNotFound gameNotFound = new GameNotFound();
		ApkDownloadSelenium apkDownloadSelenium = new ApkDownloadSelenium();
		FileMeta fileMeta = null;
		MultipartFile mpf = null;

		LinkedList<FileMeta> files = new LinkedList<FileMeta>();
		ArrayList<String> playStoreDetails = new ArrayList<String>();
		ArrayList<String> apkSiteDetails = new ArrayList<String>();
		ArrayList<ChromeDriver> closeTabs = new ArrayList<>();

		String url = ""; // Play Store URL
		String line; // line read from file and stores game name
		String temp = ""; // game name temporary
		String fileSize; // size of uploaded file
		String fileName; // name of uploaded file
		String downloadFileName; // downloading name for file.
		String fileNameID = null;
		int progress = 0; // no of game's JSOUP completed
		int count = 0; // temporary stores total no of game in file
		int id=0; // unique id for each uploaded file
		int totoalGames = 0; // total games in file
		boolean status = true; // status of APK-DL CSV created or not
		boolean psStatus = true; // status of PlayStore CSV created or not

		System.out.println("Ajax socket file controller");

		/*---------------iterator for getting file-------------*/

		Iterator<String> itr = request.getFileNames();

		// get each file
		while (itr.hasNext()) {

			// get uploaded MULTIPART file and its information
			System.out.println("request " + request.getFileNames());
			mpf = request.getFile(itr.next());
			System.out.println("sdf");
			System.out.println(mpf.getOriginalFilename() + " uploaded! ");

			fileName = mpf.getOriginalFilename();
			downloadFileName = mpf.getOriginalFilename().replace(".", "Download.");

			/*---------------end of iterator for getting files-----------------*/

			fileSize = mpf.getSize() / 1024 + " Kb";

			// storing data in file meta class
			fileMeta = new FileMeta();
			fileMeta.setFileName(fileName);
			fileMeta.setFileSize(mpf.getSize() / 1024 + " Kb");
			fileMeta.setFileType(mpf.getContentType());

			try {
				fileMeta.setBytes(mpf.getBytes());
				final InputStreamReader reader=new InputStreamReader(mpf.getInputStream());
				InputStreamReader reader2=new InputStreamReader(mpf.getInputStream());
				/*// copy uploaded file to local disk
				FileCopyUtils.copy(mpf.getBytes(),
						new FileOutputStream("/home/bridgelabz6/Pictures/files/" + fileName));*/

				// counting no of games in file
				//FileReader frCount = new FileReader("/home/bridgelabz6/Pictures/files/" + fileName);
				BufferedReader brCount = new BufferedReader(reader);
				
				while (brCount.readLine() != null) {
					count++;
				}
				totoalGames = count - 1;

				// reseting count to zero
				count = 0;

				fileMeta.setTotalGames(totoalGames);
				System.out.println("totalGames:" + totoalGames);

				//brCount.close();
				// end of counting game

				
				/*----------------------------JSOUP started--------------------------------*/

				//FileReader fr = new FileReader("/home/bridgelabz6/Pictures/files/" + fileName);
				BufferedReader br = new BufferedReader(reader2);

				line = br.readLine();
				if (line == null) {
					System.out.println("file is empty");
				} else {
					line = br.readLine();

					for (progress = 0; progress < totoalGames; progress++) {

						fileMeta.setProgress(progress);

						temp = line;
						String[] gname = line.split("\\,");

						// Separates the game name from line read from file
						line = gname[1];
						System.out.println("Game Name= " + line);

						// getting URL for game
						url = purl.findUrl(line);

						line = br.readLine();

						// exception handling if URL not found
						if (url == null) {
							//gameNotFound.addGameNotFound("Url", temp);
							System.err.println("URL Not Found");
							continue;
						} // end of handling in URL fetching

						// getting play store site data
						playStoreDetails = psdf.getPlayStoreData(url);

						// handling exception in play store details
						if (playStoreDetails.equals(null)) {
							//gameNotFound.addGameNotFound("PlayStore", temp);
							System.err.println("PlayStore Data Not Found");
						} // end of handling in play store details

						// creating CSV file of play store data
						//psStatus = psdf.createCsv(playStoreDetails, downloadFileName);

						// handling exception in creating play store data CSV
						// file
						/*if (psStatus == false) {
							gameNotFound.addGameNotFoundInFile("PlayStore", temp, downloadFileName);
						}*/

						// getting play store package name
						String pack = psdf.getPackage(playStoreDetails);

						// getting APK-DL site data
						apkSiteDetails = asdf.createApkSiteDetails(pack);

						// handling exception in APK site details
						if (apkSiteDetails == null) {
							//gameNotFound.addGameNotFoundInFile("DlApk", temp, downloadFileName);
							System.err.println("DL-APK Data Not Found");
						} // end of handling in APK-DL

						/*// creating CSV file of APK-DL site details
						status = asdf.createCsv(apkSiteDetails, downloadFileName);

						// handling exception in creating APK-DL data CSV file
						if (status == false) {
							// gameNotFound.addGameNotFoundInFile("DlApk", temp,
							// downloadFileName);
							System.out.println("eception handled");
						}*/

						// Database entry
						if (gameJsoupDao.isEmpty())
							id = 0;
						else {
							// if last filename is same as current filename
							// assign same id to game data
							if (gameJsoupDao.checkLastFileName().equals(fileName))
								id = gameJsoupDao.checkId(fileName);
							else {
								// if filename not matched, different(i.e new)
								// file is uploaded assign new id(i.e increase
								// id)
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
			// adding metaFile info to ArrayList files
			files.add(fileMeta);

/*			// concatenating ID with filename
			String fileNameID = fileName.replace(".", Integer.toString(id) + ".");
			String downloadFileNameID = downloadFileName.replace(".", Integer.toString(id) + ".");

			File oldFile = new File("/home/bridgelabz6/Pictures/files/" + fileName);
			File newFile = new File("/home/bridgelabz6/Pictures/files/" + fileNameID);

			File oldDownloadfile = new File("/home/bridgelabz6/Pictures/files/" + downloadFileName);
			File newDownloadfile = new File("/home/bridgelabz6/Pictures/files/" + downloadFileNameID);

			// renaming old file name to new
			if (oldFile.renameTo(newFile))
				System.out.println("File renamed");
			else
				System.out.println("Sorry! File can't be renamed");

			// renaming old download file name to new
			fileMeta.setDownloadFileName(downloadFileNameID);
			if (oldDownloadfile.renameTo(newDownloadfile))
				System.out.println("File renamed");
			else
				System.out.println("Sorry! File can't be renamed");
*/
			// copy downloading file to local disk
			//fileMeta.setDownBytes(FileUtils.readFileToByteArray(newDownloadfile));

			System.out.println("-----------End Of Program-----------");

		} // end of outside while

		// end of first function

		// start of download function

		try {

			System.out.println("after download file name:" + fileNameID);

			// writing download file in byte
			String data =gameJsoupDao.getFileRecords(fileNameID);

			response.setContentType(fileMeta.getFileType());
			response.setHeader("Content-disposition",
					"attachment; filename=\"" + fileMeta.getFileName() + "\"");
			FileCopyUtils.copy(data.getBytes(), response.getOutputStream());
			System.out.println("download file name:" + fileMeta.getFileName());
			System.out.println("controller response:" + response.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}// End of Downloading file function

}// End of class




