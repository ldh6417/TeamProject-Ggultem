package com.honey.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.honey.domain.BusinessBoard;

public interface BusinessBoardRepository extends JpaRepository<BusinessBoard, Long> {
	
	@EntityGraph(attributePaths = {"writer", "bItemList"})
	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.endDate > sysdate AND bb.sign = :sign AND bb.category = :category")
	List<BusinessBoard> findADPSList(@Param("category") String category, @Param("sign") boolean sign);
	
	
	@EntityGraph(attributePaths = {"writer", "bItemList"})
	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.sign = :sign AND bb.category = :category AND " +
		       "( (:searchType = 'title' AND bb.title LIKE %:keyword%) OR " +
		       "  (:searchType = 'writer' AND bb.writer LIKE %:keyword%) OR " +
		       "  (:searchType = 'content' AND bb.content LIKE %:keyword%) OR " +
		       "  (:searchType = 'all' AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%)) ) " +
		       "OR " +
		       "( (:searchType IS NULL OR :searchType = '') AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%) )")
	Page<BusinessBoard> searchByConditionAllFilter(@Param("searchType") String searchType,
			@Param("keyword") String keyword,
			@Param("sign") boolean sign,
			@Param("category") boolean category,
			Pageable pageable);
	
	@EntityGraph(attributePaths = {"writer", "bItemList"})
	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.sign = :sign AND " +
			"( (:searchType = 'title' AND bb.title LIKE %:keyword%) OR " +
			"  (:searchType = 'writer' AND bb.writer LIKE %:keyword%) OR " +
			"  (:searchType = 'content' AND bb.content LIKE %:keyword%) OR " +
			"  (:searchType = 'all' AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%)) ) " +
			"OR " +
			"( (:searchType IS NULL OR :searchType = '') AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%) )")
	Page<BusinessBoard> searchByConditionSignFilter(@Param("searchType") String searchType,
			@Param("keyword") String keyword,
			@Param("sign") boolean sign,
			Pageable pageable);
	
	@EntityGraph(attributePaths = {"writer", "bItemList"})
	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.category = :category AND " +
			"( (:searchType = 'title' AND bb.title LIKE %:keyword%) OR " +
			"  (:searchType = 'writer' AND bb.writer LIKE %:keyword%) OR " +
			"  (:searchType = 'content' AND bb.content LIKE %:keyword%) OR " +
			"  (:searchType = 'all' AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%)) ) " +
			"OR " +
			"( (:searchType IS NULL OR :searchType = '') AND (bb.title LIKE %:keyword% OR bb.writer LIKE %:keyword% OR bb.content LIKE %:keyword%) )")
	Page<BusinessBoard> searchByConditionCategoryFilter(@Param("searchType") String searchType,
			@Param("keyword") String keyword,
			@Param("category") boolean category,
			Pageable pageable);

	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.category = :category AND bb.sign = :sign ")
	Page<BusinessBoard> findAllBusinessAllFilter(Pageable pageable, @Param("sign") boolean sign,
			@Param("category") boolean category);
	
	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.category = :category ")
	Page<BusinessBoard> findAllBusinessCategoryFilter(Pageable pageable, @Param("category") boolean category);
	
	@Query("SELECT bb FROM BusinessBoard bb WHERE bb.sign = :sign ")
	Page<BusinessBoard> findAllBusinessSignFilter(Pageable pageable, @Param("sign") boolean sign);
	
	@Query("SELECT bb FROM BusinessBoard bb")
	Page<BusinessBoard> findAllBusiness(Pageable pageable);
	
}
