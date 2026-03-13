package com.honey.service;

import java.util.List;
import java.util.Optional;
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
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.repository.BoardRepository;

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
	
	
	
	@Override
	public Integer register(BoardDTO boardDTO) {

		Member member = Member.builder().no(boardDTO.getMemberNo()).build();

		Board board = Board.builder().title(boardDTO.getTitle()).writer(boardDTO.getWriter())
				.content(boardDTO.getContent()).viewCount(0).enabled(1).member(member).build();

		Board savedBoard = boardRepository.save(board);

		return savedBoard.getBoardNo();
	}
	

	@Override
	public BoardDTO read(Integer boardNo) {

		Optional<Board> result = boardRepository.findById(boardNo);
		Board board = result.orElseThrow();

		// 조회수 증가
		board.changeViewCount(board.getViewCount() + 1);

		BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);

		return boardDTO;
	}

	

	@Override
	public void modify(BoardDTO boardDTO) {

		Optional<Board> result = boardRepository.findById(boardDTO.getBoardNo());
		Board board = result.orElseThrow();

		board.changeTitle(boardDTO.getTitle());
		board.changeWriter(boardDTO.getWriter());

		boardRepository.save(board);
	}

	@Override
	public void remove(Integer boardNo) {
		Optional<Board> result = boardRepository.findById(boardNo);
		Board board = result.orElseThrow();
		
		board.changeEnabled(0);
		
		boardRepository.save(board);
		
	}

	@Override
	public PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO) {

		Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, // 1페이지 = 0
				pageRequestDTO.getSize(), Sort.by("boardNo").descending());

		Page<Board> result = boardRepository.findAllByEnabled(pageable);

		List<BoardDTO> dtoList = result.getContent().stream().map(board -> modelMapper.map(board, BoardDTO.class))
				.collect(Collectors.toList());

		long totalCount = result.getTotalElements();

		PageResponseDTO<BoardDTO> responseDTO = PageResponseDTO.<BoardDTO>withAll().dtoList(dtoList)
				.pageRequestDTO(pageRequestDTO).totalCount(totalCount).build();

		return responseDTO;
	}
}