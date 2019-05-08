package com.ETLCodeGen.model;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class UploadedFile {

	private MultipartFile file;
	List<MultipartFile> filesList;

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public List<MultipartFile> getFilesList() {
		return filesList;
	}

	public void setFilesList(List<MultipartFile> filesList) {
		this.filesList = filesList;
	}
	
	
}
