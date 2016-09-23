package com.game.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class IsDownloaded {

	/*checks file is download or not
	 *logic:incomplete download file contains extension .crxdownload. if filename in directory/folder not end with .apk
	 *returns false, else file download is completed and will return true with actual file name
	*/
	public ArrayList<String> isFileDownloaded(String fileName) {
		String flag = "false";
		ArrayList<String> info = new ArrayList<>();
		File dir = new File("/home/bridgelabz6/Downloads/apk-downloader/");
		File[] dir_contents = dir.listFiles();

		for (int i = 0; i < dir_contents.length; i++) {
			System.out.println(fileName+" is downloading");
			try {
				if (dir_contents[i].getName().contains(fileName) && dir_contents[i].getName().endsWith(".apk")) {
					flag = "true";
					info.add(dir_contents[i].getName());
					info.add(flag);
					return info;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		info.add("false");
		info.add("false");
		return info;
	}

	public void downloadCompleted(String fileName, int totoalGames) throws IOException, InterruptedException {
		FileReader st1 = new FileReader("/home/bridgelabz6/Pictures/files/" + fileName);
		BufferedReader st2 = new BufferedReader(st1);

		String folderName = fileName.replaceAll(".csv", "");

		IsDownloaded downloaded = new IsDownloaded();

		System.out.println("File Name:" + fileName + "\nTotal No:" + totoalGames);

		String dowFileName;
		int progress;

		dowFileName = st2.readLine();
		if (dowFileName == null) {
			System.out.println("file is empty");
		} else {
			//dowFileName = st2.readLine();

			for (progress = 0; progress < totoalGames; progress++) {
				dowFileName = st2.readLine();
				String[] gname = dowFileName.split("\\^");
				try {
					dowFileName = gname[5];
				} catch (Exception e) {
					System.out.println("error");
				}
				System.out.println("Game name:" + dowFileName);

				ArrayList<String> data = new ArrayList<>();
				data = downloaded.isFileDownloaded(dowFileName);

				if (data.get(1).equals("true")) {
					String movePath = "/home/bridgelabz6/Downloads/apk-downloader/";
					String targetPath = "/home/bridgelabz6/Downloads/apk-downloader/" + folderName + "/";
					File file = new File(targetPath);
					if (!file.exists())
						file.mkdirs();
					movePath = movePath.concat(data.get(0));
					targetPath = targetPath.concat(data.get(0));
					Path movefrom = FileSystems.getDefault().getPath(movePath);
					Path target = FileSystems.getDefault().getPath(targetPath);
					
					Files.move(movefrom, target, StandardCopyOption.REPLACE_EXISTING);
					System.out.println(dowFileName + " is downloaded");
				}

				else {
					data = isFileDownloaded(dowFileName);
					while (data.get(1) != "true") {
						Thread.sleep(1000);
						// downloaded.isFileDownloaded(dowFileName);
						data = isFileDownloaded(dowFileName);
					}
					String movePath = "/home/bridgelabz6/Downloads/apk-downloader/";
					String targetPath = "/home/bridgelabz6/Downloads/apk-downloader/" + folderName + "/";

					File file = new File(targetPath);
					if (!file.exists())
						file.mkdirs();

					movePath = movePath.concat(data.get(0));
					targetPath = targetPath.concat(data.get(0));

					Path movefrom = FileSystems.getDefault().getPath(movePath);
					Path target = FileSystems.getDefault().getPath(targetPath);
					Files.move(movefrom, target, StandardCopyOption.REPLACE_EXISTING);
					dowFileName = st2.readLine();
					System.out.println(dowFileName + " is downloded");

				} // end of inner for else

			} // end of for
			st2.close();
			System.out.println("completed for loop");

		}
		System.out.println("completed apk process");
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		IsDownloaded downloaded = new IsDownloaded();
		downloaded.downloadCompleted("asdDownload111.csv", 2);
	}

}
