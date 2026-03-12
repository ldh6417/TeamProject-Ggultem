package com.honey.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.honey.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	List<Member> findByStopEndDateBeforeAndEnabledIn(LocalDateTime now, List<Integer> statuses);

}
