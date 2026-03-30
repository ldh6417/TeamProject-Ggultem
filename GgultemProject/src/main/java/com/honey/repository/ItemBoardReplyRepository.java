package com.honey.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.honey.domain.ItemBoardReply;

public interface ItemBoardReplyRepository extends JpaRepository<ItemBoardReply, Long> {

	// 관리자용 동적 쿼리 (JPQL 방식)
	@Query("SELECT r FROM ItemBoardReply r " + "JOIN FETCH r.itemBoard i " + "JOIN FETCH r.member m "
			+ "WHERE (:enabled IS NULL OR r.enabled = :enabled) " + // enabled가 null이면 전체, 아니면 필터
			"AND (:keyword IS NULL OR " + "     (:searchType = 'content' AND r.content LIKE %:keyword%) OR "
			+ "     (:searchType = 'writer' AND m.nickname LIKE %:keyword%) OR "
			+ "     (:searchType = 'itemTitle' AND i.title LIKE %:keyword%) OR "
			+ "     (:searchType = 'all' AND (r.content LIKE %:keyword% OR m.nickname LIKE %:keyword% OR i.title LIKE %:keyword%)))")
	Page<ItemBoardReply> searchAdminReplyList(@Param("enabled") Integer enabled, @Param("searchType") String searchType,
			@Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT r FROM ItemBoardReply r " + "WHERE r.itemBoard.id = :id AND r.enabled >= 0 "
			+ "ORDER BY r.replyNo ASC")
	List<ItemBoardReply> getRepliesByItem(@Param("id") Long id);
}