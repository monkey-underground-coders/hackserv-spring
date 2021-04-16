package com.a6raywa1cher.hackservspring.utils;

import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

public abstract class ServiceUtils {

	public static final String DOCX_MIME = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
	public static final String PDF_MIME = "application/pdf";

	public static String getFileExtension(MultipartFile file) {
		String originalFileName = file.getOriginalFilename();
		if (originalFileName != null) {
			return originalFileName.substring(originalFileName.lastIndexOf('.'));
		} else if (file.getContentType() != null) {
			return switch (file.getContentType().toLowerCase(Locale.ROOT)) {
				case PDF_MIME -> ".pdf";
				case DOCX_MIME -> ".docx";
				default -> "";
			};
		} else {
			return "";
		}
	}
}
