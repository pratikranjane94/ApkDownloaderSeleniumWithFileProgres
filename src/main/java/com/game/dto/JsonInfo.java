package com.game.dto;

public class JsonInfo {
	private String restCall;
	private String chromeDriverPath;
	private String credentialsPath;
	private String chromeExtensionPath;
	private String apkFileDownloadFolder;
	private String csvDownloadFilePath;

	public String getRestCall() {
		return restCall;
	}

	public void setRestCall(String restCall) {
		this.restCall = restCall;
	}

	public String getChromeDriverPath() {
		return chromeDriverPath;
	}

	public void setChromeDriverPath(String chromeDriverPath) {
		this.chromeDriverPath = chromeDriverPath;
	}

	public String getCredentialsPath() {
		return credentialsPath;
	}

	public void setCredentialsPath(String credentialsPath) {
		this.credentialsPath = credentialsPath;
	}

	public String getChromeExtensionPath() {
		return chromeExtensionPath;
	}

	public void setChromeExtensionPath(String chromeExtensionPath) {
		this.chromeExtensionPath = chromeExtensionPath;
	}

	public String getApkFileDownloadFolder() {
		return apkFileDownloadFolder;
	}

	public void setApkFileDownloadFolder(String apkFileDownloadFolder) {
		this.apkFileDownloadFolder = apkFileDownloadFolder;
	}

	public String getCsvDownloadFilePath() {
		return csvDownloadFilePath;
	}

	public void setCsvDownloadFilePath(String csvDownloadFilePath) {
		this.csvDownloadFilePath = csvDownloadFilePath;
	}
}
