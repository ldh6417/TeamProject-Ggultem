package com.honey.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honey.domain.Board;

public interface BoardRepository extends JpaRepository<Board, Integer> {

    // =========================
    // 일반 사용자
    // =========================

    //  전체 조회 (검색 없이)
    @EntityGraph(attributePaths = { "boardImage" })
    @Query("""
        SELECT b FROM Board b
        WHERE b.enabled = 1
    """)
    Page<Board> findAllActive(Pageable pageable);


    // 검색 (전체 / 제목 / 내용 / 작성자)
    @EntityGraph(attributePaths = { "boardImage" })
    @Query("""
    	    SELECT b FROM Board b
    	    WHERE b.enabled = 1
    	    AND (
    	        :keyword IS NULL
    	        OR :keyword = ''
    	        OR :searchType = 'all'  
    	        OR (
    	            (:searchType = 'title' AND b.title LIKE %:keyword%) OR
    	            (:searchType = 'writer' AND b.writer LIKE %:keyword%) OR
    	            (:searchType = 'content' AND b.contentText IS NOT NULL AND b.contentText LIKE %:keyword%)
    	        )
    	    )
    	""")
    Page<Board> searchByCondition(
            @Param("searchType") String searchType,
            @Param("keyword") String keyword,
            Pageable pageable
    );


    // =========================
    // 관리자
    // =========================

    // 전체 조회 (삭제 포함)
    @EntityGraph(attributePaths = { "boardImage" })
    @Query("SELECT b FROM Board b")
    Page<Board> findAllAdmin(Pageable pageable);


    // 관리자 검색
    @EntityGraph(attributePaths = { "boardImage" })
    @Query("""
        SELECT b FROM Board b
        WHERE (:enabled IS NULL OR b.enabled = :enabled)
        AND (
            :keyword IS NULL
            OR :keyword = ''
            OR b.title LIKE %:keyword%
            OR (b.contentText IS NOT NULL AND b.contentText LIKE %:keyword%)
        )
    """)
    Page<Board> searchAllAdmin(
            @Param("enabled") Integer enabled,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}