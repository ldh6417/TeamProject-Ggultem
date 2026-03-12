package com.honey.controller;

import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.honey.dto.MemberDTO;
import com.honey.service.MemberService;
import com.honey.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/mypage")
public class MypageController {
	
	private final MemberService service;
	private final CustomFileUtil fileUtil;
	
    @GetMapping("/{no}")
    public MemberDTO getMyInfo(@PathVariable(name = "no") Long no) {
        return service.get(no);
    }

    @PostMapping("/thumbnail/{no}")
    public Map<String, String> updateMyThumbnail(@PathVariable(name="no") Long no, MemberDTO memberDTO) {
        
        List<MultipartFile> files = memberDTO.getFiles();
        List<String> uploadFileNames = fileUtil.saveFiles(files);

        memberDTO.setNo(no);
        memberDTO.setUploadFileNames(uploadFileNames);
        
        service.updateToThumbnail(memberDTO); 

        return Map.of("RESULT", "SUCCESS", "FILE_NAMES", uploadFileNames.toString());
    }
	
	@GetMapping("/view/{fileName}")
	public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName) {
		return fileUtil.getFile(fileName);
	}
}
