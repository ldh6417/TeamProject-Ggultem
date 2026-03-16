package com.honey.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.honey.domain.BoardReply;

public interface BoardReplyRepository extends JpaRepository<BoardReply, Long> {

    List<BoardReply> findByBoardBoardNoAndEnabled(Integer boardNo, Integer enabled);

    
}