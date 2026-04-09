package com.honey.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honey.domain.BusinessBoard;
import com.honey.dto.DailyStatInterface;

public interface BusinessBoardRepository extends JpaRepository<BusinessBoard, Long> {

	@EntityGraph(attributePaths = { "writer", "bItemList" })
	@Query("SELECT bb FROM BusinessBoard bb JOIN bb.member m WHERE m.bizMoney >= :minAmount AND bb.endDate > sysdate AND bb.enabled = 1 AND bb.sign = :sign AND bb.category = :category")
	List<BusinessBoard> findADPSList(@Param("category") String category, @Param("sign") boolean sign, @Param("minAmount") Long minAmount);

	///////////////////////////////////
	/// 운영중인 광고 상품 리스트 출력
	/// //////////////////////////////
	@EntityGraph(attributePaths = { "writer", "bItemList" })
	@Query("SELECT bb FROM BusinessBoard bb " + "WHERE bb.member.email = :email " + "AND bb.enabled = 1 "
			+ "AND bb.sign = :sign " + "AND bb.category = :category " + "AND ( " + // ✨ 여기서부터 검색 조건 시작 (크게 괄호로 묶기)
			"  ( (:searchType = 'title' AND bb.title LIKE %:keyword%) OR "
			+ "    (:searchType = 'writer' AND bb.writer LIKE %:keyword%) OR "
			+ "    (:searchType = 'content' AND bb.content LIKE %:keyword%) OR "
			+ "    (:searchType = 'all' AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%)) ) "
			+ "  OR "
			+ "  ( (:searchType IS NULL OR :searchType = '') AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%) ) "
			+ ")") // ✨ 검색 조건 끝
	Page<BusinessBoard> searchByConditionAllFilter(@Param("searchType") String searchType,
			@Param("keyword") String keyword, @Param("sign") boolean sign, @Param("category") String category,
			Pageable pageable, @Param("email") String email);

	@EntityGraph(attributePaths = { "writer", "bItemList" })
	@Query("SELECT bb FROM BusinessBoard bb " + "WHERE bb.member.email = :email " + "AND bb.enabled = 1 "
			+ "AND bb.sign = :sign " + "AND ( " + // 👈 검색 시작 괄호
			"  ( (:searchType = 'title' AND bb.title LIKE %:keyword%) OR "
			+ "    (:searchType = 'writer' AND bb.writer LIKE %:keyword%) OR "
			+ "    (:searchType = 'content' AND bb.content LIKE %:keyword%) OR "
			+ "    (:searchType = 'all' AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%)) ) "
			+ "  OR "
			+ "  ( (:searchType IS NULL OR :searchType = '') AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%) ) "
			+ ")") // 👈 검색 종료 괄호
	Page<BusinessBoard> searchByConditionSignFilter(@Param("searchType") String searchType,
			@Param("keyword") String keyword, @Param("sign") boolean sign, Pageable pageable,
			@Param("email") String email);

	@EntityGraph(attributePaths = { "writer", "bItemList" })
	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.member.email = :email AND bb.enabled = 1 AND bb.category = :category AND "
			+ "( (:searchType = 'title' AND bb.title LIKE %:keyword%) OR "
			+ "  (:searchType = 'writer' AND bb.writer LIKE %:keyword%) OR "
			+ "  (:searchType = 'content' AND bb.content LIKE %:keyword%) OR "
			+ "  (:searchType = 'all' AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%)) ) "
			+ "OR "
			+ "( (:searchType IS NULL OR :searchType = '') AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%) )")
	Page<BusinessBoard> searchByConditionCategoryFilter(@Param("searchType") String searchType,
			@Param("keyword") String keyword, @Param("category") String category, Pageable pageable,
			@Param("email") String email);

	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.member.email = :email AND bb.category = :category AND bb.sign = :sign AND bb.enabled = 1")
	Page<BusinessBoard> findAllBusinessAllFilter(Pageable pageable, @Param("sign") boolean sign,
			@Param("category") String category, @Param("email") String email);

	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.member.email = :email AND bb.category = :category AND bb.enabled = 1 ")
	Page<BusinessBoard> findAllBusinessCategoryFilter(Pageable pageable, @Param("category") String category,
			@Param("email") String email);

	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.member.email = :email AND bb.sign = :sign AND bb.enabled = 1 ")
	Page<BusinessBoard> findAllBusinessSignFilter(Pageable pageable, @Param("sign") boolean sign,
			@Param("email") String email);

	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.member.email = :email AND bb.enabled = 1")
	Page<BusinessBoard> findAllBusiness(Pageable pageable, @Param("email") String email);

	///////////////////////////////////
	/// 휴지통에 있는 광고 상품 리스트 출력
	/// //////////////////////////////
	@EntityGraph(attributePaths = { "writer", "bItemList" })
	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.member.email = :email AND bb.enabled = 0 AND bb.sign = :sign AND bb.category = :category AND "
			+ "( (:searchType = 'title' AND bb.title LIKE %:keyword%) OR "
			+ "  (:searchType = 'writer' AND bb.writer LIKE %:keyword%) OR "
			+ "  (:searchType = 'content' AND bb.content LIKE %:keyword%) OR "
			+ "  (:searchType = 'all' AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%)) ) "
			+ "OR "
			+ "( (:searchType IS NULL OR :searchType = '') AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%) )")
	Page<BusinessBoard> searchByConditionDeleteFilter(@Param("searchType") String searchType,
			@Param("keyword") String keyword, @Param("sign") boolean sign, @Param("category") String category,
			Pageable pageable, @Param("email") String email);

	@EntityGraph(attributePaths = { "writer", "bItemList" })
	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.member.email = :email AND bb.enabled = 0 AND bb.sign = :sign AND "
			+ "( (:searchType = 'title' AND bb.title LIKE %:keyword%) OR "
			+ "  (:searchType = 'writer' AND bb.writer LIKE %:keyword%) OR "
			+ "  (:searchType = 'content' AND bb.content LIKE %:keyword%) OR "
			+ "  (:searchType = 'all' AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%)) ) "
			+ "OR "
			+ "( (:searchType IS NULL OR :searchType = '') AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%) )")
	Page<BusinessBoard> searchByConditionDeleteSignFilter(@Param("searchType") String searchType,
			@Param("keyword") String keyword, @Param("sign") boolean sign, Pageable pageable,
			@Param("email") String email);

	@EntityGraph(attributePaths = { "writer", "bItemList" })
	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.member.email = :email AND bb.enabled = 0 AND bb.category = :category AND "
			+ "( (:searchType = 'title' AND bb.title LIKE %:keyword%) OR "
			+ "  (:searchType = 'writer' AND bb.writer LIKE %:keyword%) OR "
			+ "  (:searchType = 'content' AND bb.content LIKE %:keyword%) OR "
			+ "  (:searchType = 'all' AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%)) ) "
			+ "OR "
			+ "( (:searchType IS NULL OR :searchType = '') AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%) )")
	Page<BusinessBoard> searchByConditionDeleteCategoryFilter(@Param("searchType") String searchType,
			@Param("keyword") String keyword, @Param("category") String category, Pageable pageable,
			@Param("email") String email);

	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.member.email = :email AND bb.category = :category AND bb.sign = :sign AND bb.enabled = 0")
	Page<BusinessBoard> findAllBusinessDeleteFilter(Pageable pageable, @Param("sign") boolean sign,
			@Param("category") String category, @Param("email") String email);

	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.member.email = :email AND bb.category = :category AND bb.enabled = 0 ")
	Page<BusinessBoard> findAllBusinessDeleteCategoryFilter(Pageable pageable, @Param("category") String category,
			@Param("email") String email);

	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.member.email = :email AND bb.sign = :sign AND bb.enabled = 0 ")
	Page<BusinessBoard> findAllBusinessDeleteSignFilter(Pageable pageable, @Param("sign") boolean sign,
			@Param("email") String email);

	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.member.email = :email AND bb.enabled = 0")
	Page<BusinessBoard> findDeleteBusiness(Pageable pageable, @Param("email") String email);

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/// Admin 광고 상품 리스트 출력
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////
/// 운영중인 광고 상품 리스트 출력
/// //////////////////////////////

	@EntityGraph(attributePaths = { "writer", "bItemList" })
	@Query("SELECT bb FROM BusinessBoard bb " + "WHERE bb.enabled = 1 "
			+ "AND bb.sign = :sign " + "AND bb.category = :category " + "AND ( " + // ✨ 여기서부터 검색 조건 시작 (크게 괄호로 묶기)
			"  ( (:searchType = 'title' AND bb.title LIKE %:keyword%) OR "
			+ "    (:searchType = 'writer' AND bb.writer LIKE %:keyword%) OR "
			+ "    (:searchType = 'content' AND bb.content LIKE %:keyword%) OR "
			+ "    (:searchType = 'all' AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%)) ) "
			+ "  OR "
			+ "  ( (:searchType IS NULL OR :searchType = '') AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%) ) "
			+ ")") // ✨ 검색 조건 끝
	Page<BusinessBoard> adminSearchByConditionAllFilter(@Param("searchType") String searchType,
			@Param("keyword") String keyword, @Param("sign") boolean sign, @Param("category") String category,
			Pageable pageable);

	@EntityGraph(attributePaths = { "writer", "bItemList" })
	@Query("SELECT bb FROM BusinessBoard bb " + "WHERE bb.enabled = 1 "
			+ "AND bb.sign = :sign " + "AND ( " + // 👈 검색 시작 괄호
			"  ( (:searchType = 'title' AND bb.title LIKE %:keyword%) OR "
			+ "    (:searchType = 'writer' AND bb.writer LIKE %:keyword%) OR "
			+ "    (:searchType = 'content' AND bb.content LIKE %:keyword%) OR "
			+ "    (:searchType = 'all' AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%)) ) "
			+ "  OR "
			+ "  ( (:searchType IS NULL OR :searchType = '') AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%) ) "
			+ ")") // 👈 검색 종료 괄호
	Page<BusinessBoard> adminSearchByConditionSignFilter(@Param("searchType") String searchType,
			@Param("keyword") String keyword, @Param("sign") boolean sign, Pageable pageable);

	@EntityGraph(attributePaths = { "writer", "bItemList" })
	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.enabled = 1 AND bb.category = :category AND "
			+ "( (:searchType = 'title' AND bb.title LIKE %:keyword%) OR "
			+ "  (:searchType = 'writer' AND bb.writer LIKE %:keyword%) OR "
			+ "  (:searchType = 'content' AND bb.content LIKE %:keyword%) OR "
			+ "  (:searchType = 'all' AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%)) ) "
			+ "OR "
			+ "( (:searchType IS NULL OR :searchType = '') AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%) )")
	Page<BusinessBoard> adminSearchByConditionCategoryFilter(@Param("searchType") String searchType,
			@Param("keyword") String keyword, @Param("category") String category, Pageable pageable);

	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.category = :category AND bb.sign = :sign AND bb.enabled = 1")
	Page<BusinessBoard> adminFindAllBusinessAllFilter(Pageable pageable, @Param("sign") boolean sign,
			@Param("category") String category);

	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.category = :category AND bb.enabled = 1 ")
	Page<BusinessBoard> adminFindAllBusinessCategoryFilter(Pageable pageable, @Param("category") String category);

	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.sign = :sign AND bb.enabled = 1 ")
	Page<BusinessBoard> adminFindAllBusinessSignFilter(Pageable pageable, @Param("sign") boolean sign);

	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.enabled = 1")
	Page<BusinessBoard> adminFindAllBusiness(Pageable pageable);

///////////////////////////////////
/// 휴지통에 있는 광고 상품 리스트 출력
/// //////////////////////////////
	@EntityGraph(attributePaths = { "writer", "bItemList" })
	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.enabled = 0 AND bb.sign = :sign AND bb.category = :category AND "
			+ "( (:searchType = 'title' AND bb.title LIKE %:keyword%) OR "
			+ "  (:searchType = 'writer' AND bb.writer LIKE %:keyword%) OR "
			+ "  (:searchType = 'content' AND bb.content LIKE %:keyword%) OR "
			+ "  (:searchType = 'all' AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%)) ) "
			+ "OR "
			+ "( (:searchType IS NULL OR :searchType = '') AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%) )")
	Page<BusinessBoard> adminSearchByConditionDeleteFilter(@Param("searchType") String searchType,
			@Param("keyword") String keyword, @Param("sign") boolean sign, @Param("category") String category,
			Pageable pageable);

	@EntityGraph(attributePaths = { "writer", "bItemList" })
	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.enabled = 0 AND bb.sign = :sign AND "
			+ "( (:searchType = 'title' AND bb.title LIKE %:keyword%) OR "
			+ "  (:searchType = 'writer' AND bb.writer LIKE %:keyword%) OR "
			+ "  (:searchType = 'content' AND bb.content LIKE %:keyword%) OR "
			+ "  (:searchType = 'all' AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%)) ) "
			+ "OR "
			+ "( (:searchType IS NULL OR :searchType = '') AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%) )")
	Page<BusinessBoard> adminSearchByConditionDeleteSignFilter(@Param("searchType") String searchType,
			@Param("keyword") String keyword, @Param("sign") boolean sign, Pageable pageable);

	@EntityGraph(attributePaths = { "writer", "bItemList" })
	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.enabled = 0 AND bb.category = :category AND "
			+ "( (:searchType = 'title' AND bb.title LIKE %:keyword%) OR "
			+ "  (:searchType = 'writer' AND bb.writer LIKE %:keyword%) OR "
			+ "  (:searchType = 'content' AND bb.content LIKE %:keyword%) OR "
			+ "  (:searchType = 'all' AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%)) ) "
			+ "OR "
			+ "( (:searchType IS NULL OR :searchType = '') AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%) )")
	Page<BusinessBoard> adminSearchByConditionDeleteCategoryFilter(@Param("searchType") String searchType,
			@Param("keyword") String keyword, @Param("category") String category, Pageable pageable);

	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.category = :category AND bb.sign = :sign AND bb.enabled = 0")
	Page<BusinessBoard> adminFindAllBusinessDeleteFilter(Pageable pageable, @Param("sign") boolean sign, @Param("category") String category);

	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.category = :category AND bb.enabled = 0 ")
	Page<BusinessBoard> adminFindAllBusinessDeleteCategoryFilter(Pageable pageable, @Param("category") String category);

	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.sign = :sign AND bb.enabled = 0 ")
	Page<BusinessBoard> adminFindAllBusinessDeleteSignFilter(Pageable pageable, @Param("sign") boolean sign);

	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.enabled = 0")
	Page<BusinessBoard> adminFindDeleteBusiness(Pageable pageable);

	// 1. 카테고리별 총 클릭수 (null일 경우 0을 반환하도록 coalesce 처리)
	@Query("select coalesce(sum(b.viewCount), 0) from BusinessBoard b where b.member.email = :email and b.category = :category")
	long sumViewCountByCategory(@Param("email") String email, @Param("category") String category);

	// 2. 카테고리별 광고 개수
	@Query("select count(b) from BusinessBoard b where b.member.email = :email and b.category = :category")
	long countByCategory(@Param("email") String email, @Param("category") String category);

	// 3. 월별 통계 (연-월 그룹화)
	// 경로를 MonthlyStat으로 수정 (달러 기호 $ 제거)
	@Query("SELECT FUNCTION('to_char', cl.clickDate, 'YYYY-MM-DD') as day, " + "COUNT(cl) as totalCount, "
			+ "COALESCE(SUM(CASE WHEN b.category = 'powerlink' THEN 1 ELSE 0 END), 0L) as powerLinkClick, "
			+ "COALESCE(SUM(CASE WHEN b.category = 'powershoping' THEN 1 ELSE 0 END), 0L) as powerShoppingClick "
			+ "FROM ClickLog cl " + "JOIN cl.board b " + "WHERE b.member.email = :email "
			+ "AND cl.clickDate BETWEEN :start AND :end " + "GROUP BY FUNCTION('to_char', cl.clickDate, 'YYYY-MM-DD') "
			+ "ORDER BY day ASC")
	List<DailyStatInterface> getDailyStats(@Param("email") String email, @Param("start") LocalDateTime start,
			@Param("end") LocalDateTime end);
	
	@Query("SELECT COALESCE(SUM(bb.viewCount), 0) " + 
		       "FROM BusinessBoard bb " + 
		       "WHERE bb.member.email = :email")
		Integer getTotalClick(@Param("email") String email);
	
	@Query("SELECT count(*) " + 
			"FROM ClickLog cl " + 
			"WHERE cl.userEmail = :email " + 
			"AND cl.clickDate >= :startOfToday")
	Integer getTodayClick(@Param("email") String email,  @Param("startOfToday") java.time.LocalDateTime startOfToday);
	
	@Query("SELECT count(*) " + 
			"FROM BusinessBoard bb " +
			"WHERE bb.sign = true " +
			"AND bb.endDate >= :startOfToday")
	long countActiveAds(@Param("startOfToday") java.time.LocalDateTime startOfToday);

}
