package com.honey.service;

import com.honey.dto.BoardDTO;
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;

public interface BoardService {

	// 게시글 등록
	public Integer register(BoardDTO boardDTO) ;

	// 게시글 조회
	public BoardDTO read(Integer boardNo) ;

	// 게시글 수정
	public void modify(BoardDTO boardDTO) ;

	// 게시글 삭제
	public void remove(Integer boardNo) ;

	// 게시글 목록
	public PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO) ;

}
