package com.ETLCodeGen.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.ETLCodeGen.model.UploadedFile;

public class FileValidator implements Validator {

	@Override
	public boolean supports(Class<?> arg0) {
		return false;
	}

	@Override
	public void validate(Object uploadedFile, Errors errors) {

		UploadedFile file = (UploadedFile) uploadedFile;

		if (file.getFile().getSize() == 0) {
			errors.rejectValue("file", "uploadForm.selectFile",
					"Please select a file!");
		/*}else if(!(file.getFile().getContentType().equalsIgnoreCase("application/vnd.ms-excel") || file.getFile().getContentType().equalsIgnoreCase("application/octet-stream"))) {
			errors.rejectValue("file", "uploadForm.selectFile",
					"Incorrect file type. Please upload in .xls format !");*/
		}else if(!(file.getFile().getContentType().equalsIgnoreCase("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") || file.getFile().getContentType().equalsIgnoreCase("application/vnd.ms-excel.sheet.macroEnabled.12") || file.getFile().getContentType().equalsIgnoreCase("application/octet-stream"))) {
			errors.rejectValue("file", "uploadForm.selectFile",
					"Incorrect file type. Please upload in .xlsx and .xlsm format !");
		}
	}

}
