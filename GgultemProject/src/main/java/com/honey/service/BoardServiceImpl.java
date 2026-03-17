<<<<<<< feature/cart
//package com.honey.service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.modelmapper.ModelMapper;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//
//import com.honey.domain.Board;
//import com.honey.domain.Member;
//import com.honey.dto.BoardDTO;
//import com.honey.dto.PageRequestDTO;
//import com.honey.dto.PageResponseDTO;
//import com.honey.dto.SearchDTO;
//import com.honey.repository.BoardRepository;
//import com.honey.util.CustomFileUtil;
//
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//@Service
//@Transactional
//@Slf4j
//@RequiredArgsConstructor
//public class BoardServiceImpl implements BoardService {
//
//	private final ModelMapper modelMapper;
//    private final CustomFileUtil fileUtil;
//    private final BoardRepository boardRepository;
//
//    // 게시글 등록
//    @Override
//    public Integer register(BoardDTO boardDTO) {
//
//        Member member = Member.builder()
//                .no(boardDTO.getMemberNo())
//                .build();
//
//        Board board = Board.builder()
//                .title(boardDTO.getTitle())
//                .writer(boardDTO.getWriter())
//                .content(boardDTO.getContent())
//                .viewCount(0)
//                .enabled(1)
//                .member(member)
//                .build();
//
//        // ⭐ 파일 저장
//        List<String> uploadFileNames = fileUtil.saveFiles(boardDTO.getFiles());
//
//        if(uploadFileNames != null){
//            uploadFileNames.forEach(fileName -> {
//                board.addImageString(fileName);
//            });
//        }
//
//        Board savedBoard = boardRepository.save(board);
//
//        return savedBoard.getBoardNo();
//    }
//
//    // 게시글 조회
//    @Override
//    public BoardDTO get(Integer boardNo) {
//
//        Board board = boardRepository.findById(boardNo).orElseThrow();
//
//        board.changeViewCount(board.getViewCount() + 1);
//
//        BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);
//
//        // ⭐ 이미지 파일 이름 DTO에 넣기
//        List<String> fileNames = board.getBoardImage()
//                .stream()
//                .map(img -> img.getFileName())
//                .toList();
//
//        boardDTO.setUploadFileNames(fileNames);
//
//        return boardDTO;
//    }
//
//    // 게시글 수정
//    @Override
//    public void modify(BoardDTO boardDTO) {
//
//        Board board = boardRepository.findById(boardDTO.getBoardNo()).orElseThrow();
//
//        // 제목 수정
//        board.changeTitle(boardDTO.getTitle());
//        board.changeWriter(boardDTO.getWriter());
//
//        // 기존 이미지 목록
//        List<String> oldFileNames = board.getBoardImage()
//                .stream()
//                .map(img -> img.getFileName())
//                .toList();
//
//        // 새로 업로드된 파일
//        List<String> newUploadFileNames = fileUtil.saveFiles(boardDTO.getFiles());
//
//        // 화면에서 유지되는 파일
//        List<String> uploadedFileNames = boardDTO.getUploadFileNames();
//
//        if(newUploadFileNames != null){
//            uploadedFileNames.addAll(newUploadFileNames);
//        }
//
//        // DB 이미지 초기화
//        board.clearList();
//
//        if(uploadedFileNames != null){
//            uploadedFileNames.forEach(fileName -> {
//                board.addImageString(fileName);
//            });
//        }
//
//        boardRepository.save(board);
//
//        // 삭제할 파일 찾기
//        if(oldFileNames != null){
//            List<String> removeFiles = oldFileNames.stream()
//                    .filter(fileName -> uploadedFileNames.indexOf(fileName) == -1)
//                    .toList();
//
//            fileUtil.deleteFiles(removeFiles);
//        }
//    }
//
//    // 게시글 삭제
//    @Override
//    public void remove(Integer boardNo) {
//
//        Board board = boardRepository.findById(boardNo).orElseThrow();
//
//        // 파일 삭제
//        List<String> fileNames = board.getBoardImage()
//                .stream()
//                .map(img -> img.getFileName())
//                .toList();
//
//        if(!fileNames.isEmpty()){
//            fileUtil.deleteFiles(fileNames);
//        }
//        
//        board.changeEnabled(0);
//
//        // 게시글 삭제
//        boardRepository.save(board);
//    }
//
//    // 게시글 목록
//    @Override
//    public PageResponseDTO<BoardDTO> list(SearchDTO searchDTO) {
//
//        Pageable pageable = PageRequest.of(
//        		searchDTO.getPage() - 1,
//        		searchDTO.getSize(),
//                Sort.by("boardNo").descending()
//        );
//
//        Page<Board> result = null;
//        if(searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {
//        	result = boardRepository.searchByCondition(
//					searchDTO.getSearchType(),
//					searchDTO.getKeyword(),
//					pageable);
//        } else {
//        	result = boardRepository.findAllByEnabled(pageable);
//        }
//
//        List<BoardDTO> dtoList = result.getContent().stream()
//                .map(board -> {
//
//                    BoardDTO dto = modelMapper.map(board, BoardDTO.class);
//
//                    List<String> fileNames = board.getBoardImage()
//                            .stream()
//                            .map(img -> img.getFileName())
//                            .toList();
//
//                    dto.setUploadFileNames(fileNames);
//
//                    return dto;
//
//                }).collect(Collectors.toList());
//
//        return PageResponseDTO.<BoardDTO>withAll()
//                .dtoList(dtoList)
//                .pageRequestDTO(searchDTO)
//                .totalCount(result.getTotalElements())
//                .build();
//    }
//}
=======
package com.honey.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.honey.domain.Board;
import com.honey.domain.Member;
import com.honey.dto.BoardDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.repository.BoardRepository;
import com.honey.repository.MemberRepository;
import com.honey.util.CustomFileUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

	private final ModelMapper modelMapper;
	private final CustomFileUtil fileUtil;
	private final BoardRepository boardRepository;
	private final MemberRepository memberRepository;

	// 게시글 등록
	@Override
	public Integer register(BoardDTO boardDTO) {

		Member member = memberRepository.findById(boardDTO.getEmail()).orElseThrow();

		Board board = Board.builder().title(boardDTO.getTitle()).writer(member.getNickname()) //  프론트 값 안 믿고 서버에서 설정
				.content(boardDTO.getContent()).viewCount(0).enabled(1).member(member).build();

		List<String> uploadFileNames = fileUtil.saveFiles(boardDTO.getFiles());

		if (uploadFileNames != null) {
			uploadFileNames.forEach(board::addImageString);
		}

		return boardRepository.save(board).getBoardNo();
	}

	// 게시글 조회
	@Override
	public BoardDTO get(Integer boardNo) {

		Board board = boardRepository.findById(boardNo).orElseThrow();

		board.changeViewCount(board.getViewCount() + 1);

		BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);

		List<String> fileNames = board.getBoardImage().stream().map(img -> img.getFileName()).toList();

		boardDTO.setUploadFileNames(fileNames);

		return boardDTO;
	}

	// 게시글 수정
	@Override
	public void modify(BoardDTO boardDTO) {

		Board board = boardRepository.findById(boardDTO.getBoardNo()).orElseThrow();

		board.changeTitle(boardDTO.getTitle());
		board.changeWriter(boardDTO.getWriter());

		List<String> oldFileNames = board.getBoardImage().stream().map(img -> img.getFileName()).toList();

		List<String> newUploadFileNames = fileUtil.saveFiles(boardDTO.getFiles());

		List<String> uploadedFileNames = boardDTO.getUploadFileNames() != null
				? new ArrayList<>(boardDTO.getUploadFileNames())
				: new ArrayList<>();

		if (newUploadFileNames != null) {
			uploadedFileNames.addAll(newUploadFileNames);
		}

		board.clearList();

		uploadedFileNames.forEach(board::addImageString);

		boardRepository.save(board);

		//  삭제 파일 처리
		List<String> removeFiles = oldFileNames.stream().filter(fileName -> !uploadedFileNames.contains(fileName))
				.toList();

		fileUtil.deleteFiles(removeFiles);
	}

	// 게시글 삭제 (논리삭제 + 파일삭제)
	@Override
	public void remove(Integer boardNo) {

		Board board = boardRepository.findById(boardNo).orElseThrow();

		List<String> fileNames = board.getBoardImage().stream().map(img -> img.getFileName()).toList();

		if (!fileNames.isEmpty()) {
			fileUtil.deleteFiles(fileNames);
		}

		board.changeEnabled(0);

		boardRepository.save(board);
	}

	// 게시글 목록
	@Override
	public PageResponseDTO<BoardDTO> list(SearchDTO searchDTO) {

		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize(),
				Sort.by("boardNo").descending());

		Page<Board> result;

		if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {

			result = boardRepository.searchByCondition(searchDTO.getSearchType(), searchDTO.getKeyword(), pageable);

		} else {

			
			result = boardRepository.findAllActive(pageable);
		}

		List<BoardDTO> dtoList = result.getContent().stream().map(board -> {

			BoardDTO dto = modelMapper.map(board, BoardDTO.class);

			List<String> fileNames = board.getBoardImage().stream().map(img -> img.getFileName()).toList();

			dto.setUploadFileNames(fileNames);

			return dto;

		}).collect(Collectors.toList());

		return PageResponseDTO.<BoardDTO>withAll().dtoList(dtoList).pageRequestDTO(searchDTO)
				.totalCount(result.getTotalElements()).build();
	}
	
	
}
>>>>>>> main
