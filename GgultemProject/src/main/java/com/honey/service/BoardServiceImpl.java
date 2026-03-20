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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final ModelMapper modelMapper;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    // =========================
    // 게시글 등록
    // =========================
    @Override
    public Integer register(BoardDTO boardDTO) {

        Member member = memberRepository.findById(boardDTO.getEmail()).orElseThrow();

        Board board = Board.builder()
                .title(boardDTO.getTitle())
                .writer(member.getNickname())
                .content(boardDTO.getContent())
                .viewCount(0)
                .enabled(1)
                .member(member)
                .build();

        // Controller에서 전달받은 파일명 사용
        List<String> uploadFileNames = boardDTO.getUploadFileNames();

        if (uploadFileNames != null) {
            uploadFileNames.forEach(board::addImageString);
        }

        return boardRepository.save(board).getBoardNo();
    }

    // =========================
    // 게시글 조회
    // =========================
    @Override
    public BoardDTO get(Integer boardNo) {

        Board board = boardRepository.findById(boardNo).orElseThrow();

        board.changeViewCount(board.getViewCount() + 1);

        BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);

        List<String> fileNames = board.getBoardImage()
                .stream()
                .map(img -> img.getFileName())
                .toList();

        boardDTO.setUploadFileNames(fileNames);

        return boardDTO;
    }

    // =========================
    // 게시글 수정
    // =========================
    @Override
    public void modify(BoardDTO boardDTO) {

        Board board = boardRepository.findById(boardDTO.getBoardNo()).orElseThrow();

        board.changeTitle(boardDTO.getTitle());
        board.changeWriter(boardDTO.getWriter());

        // Controller에서 이미 처리된 파일명 사용
        List<String> uploadFileNames = boardDTO.getUploadFileNames();

        board.clearList();

        if (uploadFileNames != null) {
            uploadFileNames.forEach(board::addImageString);
        }

        boardRepository.save(board);
    }

    // =========================
    // 게시글 삭제 (논리삭제)
    // =========================
    @Override
    public void remove(Integer boardNo) {

        Board board = boardRepository.findById(boardNo).orElseThrow();

        board.changeEnabled(0);

        boardRepository.save(board);
    }

    // =========================
    // 게시글 목록
    // =========================
    @Override
    public PageResponseDTO<BoardDTO> list(SearchDTO searchDTO) {

        Pageable pageable = PageRequest.of(
                searchDTO.getPage() - 1,
                searchDTO.getSize(),
                Sort.by("boardNo").descending()
        );

        Page<Board> result;

        if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {

            result = boardRepository.searchByCondition(
                    searchDTO.getSearchType(),
                    searchDTO.getKeyword(),
                    pageable
            );

        } else {

            result = boardRepository.findAllActive(pageable);
        }

        List<BoardDTO> dtoList = result.getContent().stream().map(board -> {

            BoardDTO dto = modelMapper.map(board, BoardDTO.class);

            List<String> fileNames = board.getBoardImage()
                    .stream()
                    .map(img -> img.getFileName())
                    .toList();

            dto.setUploadFileNames(fileNames);

            return dto;

        }).collect(Collectors.toList());

        return PageResponseDTO.<BoardDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(searchDTO)
                .totalCount(result.getTotalElements())
                .build();
    }

    // =========================
    // 관리자
    // =========================
    @Override
    public PageResponseDTO<BoardDTO> adminList(SearchDTO searchDTO) {

        Pageable pageable = PageRequest.of(
                searchDTO.getPage() - 1,
                searchDTO.getSize(),
                Sort.by("boardNo").descending()
        );

        Page<Board> result;

        if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {

            result = boardRepository.searchByConditionAdmin(
                    searchDTO.getSearchType(),
                    searchDTO.getKeyword(),
                    pageable
            );

        } else {

            result = boardRepository.findAll(pageable);
        }

        List<BoardDTO> dtoList = result.getContent().stream().map(board -> {

            BoardDTO dto = modelMapper.map(board, BoardDTO.class);

            List<String> fileNames = board.getBoardImage()
                    .stream()
                    .map(img -> img.getFileName())
                    .toList();

            dto.setUploadFileNames(fileNames);

            return dto;

        }).toList();

        return PageResponseDTO.<BoardDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(searchDTO)
                .totalCount(result.getTotalElements())
                .build();
    }

    @Override
    public void adminRemove(Integer boardNo) {
        Board board = boardRepository.findById(boardNo).orElseThrow();
        board.changeEnabled(0);
    }
}