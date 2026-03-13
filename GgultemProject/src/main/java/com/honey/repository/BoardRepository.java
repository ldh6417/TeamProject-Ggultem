package com.honey.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.honey.domain.Board;

public interface BoardRepository extends JpaRepository<Board, Integer> {
	
	@Query("select b from Board b where enabled = 1")
	Page<Board> findAllByEnabled(Pageable pageable);

}
