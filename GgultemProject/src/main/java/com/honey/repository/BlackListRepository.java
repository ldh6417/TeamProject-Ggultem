package com.honey.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.honey.domain.BlackList;

public interface BlackListRepository extends JpaRepository<BlackList, Long> {
	
	@Query("select c from BlackList c where enabled = 1")
	Page<BlackList> findAllByEnabled(Pageable pageable);

}
