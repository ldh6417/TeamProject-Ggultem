package com.honey.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honey.domain.Board;
import com.honey.dto.BoardDTO;

public interface BoardRepository extends JpaRepository<Board, Integer> {

	// =========================
	// 일반 사용자 검색 
	// =========================
	@Query("""
			    SELECT b,
			           (SELECT COUNT(r)
			            FROM BoardReply r
			            WHERE r.board = b AND r.enabled = 1)
			    FROM Board b
			    WHERE b.enabled = 1
			    AND (
			        :keyword IS NULL OR :keyword = ''
			        OR (
			            (:searchType = 'title' AND b.title LIKE CONCAT('%', :keyword, '%'))
			            OR (:searchType = 'writer' AND b.writer LIKE CONCAT('%', :keyword, '%'))
			            OR (:searchType = 'content' AND b.contentText LIKE CONCAT('%', :keyword, '%'))
			            OR (
			                (:searchType = 'all' OR :searchType IS NULL OR :searchType = '')
			                AND (
			                    b.title LIKE CONCAT('%', :keyword, '%')
			                    OR b.writer LIKE CONCAT('%', :keyword, '%')
			                    OR b.contentText LIKE CONCAT('%', :keyword, '%')
			                )
			            )
			        )
			    )
			    ORDER BY b.boardNo DESC
			""")
	Page<Object[]> searchByCondition(@Param("searchType") String searchType, @Param("keyword") String keyword,
			Pageable pageable);
	// =========================
	// 관리자
	// =========================

	// 관리자 전체 조회
	@Query("""
			    SELECT b,
			           (SELECT COUNT(r)
			            FROM BoardReply r
			            WHERE r.board = b AND r.enabled = 1)
			    FROM Board b
			    ORDER BY b.boardNo DESC
			""")
	Page<Object[]> findAllAdmin(Pageable pageable);

	// 관리자 검색
	@Query("""
			    SELECT b,
			           (SELECT COUNT(r)
			            FROM BoardReply r
			            WHERE r.board = b AND r.enabled = 1)
			    FROM Board b
			    WHERE (:enabled IS NULL OR b.enabled = :enabled)
			    AND (
			        :keyword IS NULL
			        OR :keyword = ''
			        OR b.title LIKE %:keyword%
			        OR (b.contentText IS NOT NULL AND b.contentText LIKE %:keyword%)
			    )
			    ORDER BY b.boardNo DESC
			""")
	Page<Object[]> searchAllAdmin(@Param("enabled") Integer enabled, @Param("keyword") String keyword,
			Pageable pageable);

	List<Board> findTop5ByOrderByRegDateDesc();
}