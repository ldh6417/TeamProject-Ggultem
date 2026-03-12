package com.honey.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
@RequiredArgsConstructor
public class CustomFileUtil {
	
	@Value("${com.honey.upload.path}")
	private String uploadPath;
	
	@PostConstruct
	public void init() {
		File tempFolder = new File(uploadPath);
		
		if(tempFolder.exists() == false) {
			tempFolder.mkdir();
		}
	}
	
	public List<String> saveFiles(List<MultipartFile> files) throws RuntimeException {
		if (files == null || files.isEmpty()) { // size() == 0 대신 isEmpty() 권장
			return null;
		}

		List<String> uploadNames = new ArrayList<>();

		for (MultipartFile multipartFile : files) {
			String savedName = UUID.randomUUID().toString() + "_" + multipartFile.getOriginalFilename();
			Path savePath = Paths.get(uploadPath, savedName);

			try {
				Files.copy(multipartFile.getInputStream(), savePath);
				uploadNames.add(savedName);
			} catch (IOException e) {
				throw new RuntimeException("File save error: " + e.getMessage());
			}
		}
		return uploadNames;
	}
	
}
