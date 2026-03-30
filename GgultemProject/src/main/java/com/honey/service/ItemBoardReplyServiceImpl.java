package com.honey.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.honey.domain.ItemBoard;
import com.honey.domain.ItemBoardReply;
import com.honey.domain.Member;
import com.honey.dto.ItemBoardReplyDTO;
import com.honey.dto.ItemBoardSearchDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.repository.ItemBoardReplyRepository;
import com.honey.repository.ItemBoardRepository;
import com.honey.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemBoardReplyServiceImpl implements ItemBoardReplyService {

	private final ItemBoardReplyRepository itemBoardReplyRepository;
	private final ItemBoardRepository itemBoardRepository;
	private final MemberRepository memberRepository;

	@Override
	public List<ItemBoardReplyDTO> list(Long itemId) {

		List<ItemBoardReply> replyList = itemBoardReplyRepository.getRepliesByItem(itemId);

		Map<Long, ItemBoardReplyDTO> dtoMap = replyList.stream()
				.map(reply -> ItemBoardReplyDTO.builder().replyNo(reply.getReplyNo())
						.itemId(reply.getItemBoard().getId()).email(reply.getMember().getEmail())
						.content(reply.getContent())
						.regDate(reply.getRegDate())
						.parentReplyNo(reply.getParent() != null ? reply.getParent().getReplyNo() : null).enabled(reply.getEnabled())
						.itemTitle(reply.getItemBoard().getTitle()).nickname(reply.getMember().getNickname()).build())
				.collect(Collectors.toMap(ItemBoardReplyDTO::getReplyNo, dto -> dto));

		List<ItemBoardReplyDTO> result = new ArrayList<>();

		for (ItemBoardReplyDTO dto : dtoMap.values()) {

			if (dto.getParentReplyNo() == null) {
				result.add(dto);
			} else {
				ItemBoardReplyDTO parent = dtoMap.get(dto.getParentReplyNo());
				if (parent != null) {
					parent.getChildList().add(dto);
				}
			}
		}

		return result;
	}

	// 2. [추가] 관리자용: 전체 댓글을 페이징/검색해서 보여주는 평면 리스트
	@Override
	public PageResponseDTO<ItemBoardReplyDTO> adminList(ItemBoardSearchDTO searchDTO) {

	    // 1. 페이징 설정
	    Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize(),
	            Sort.by("replyNo").descending());

	    // 2. 검색어 처리 (빈 문자열이면 null로 처리해서 쿼리에서 무시되게 함)
	    String keyword = (searchDTO.getKeyword() != null && !searchDTO.getKeyword().trim().isEmpty()) 
	                     ? searchDTO.getKeyword() : null;
	    String searchType = (searchDTO.getSearchType() != null) ? searchDTO.getSearchType() : "all";
	    Integer enabled = searchDTO.getEnabled(); // null이면 전체보기

	    // 3. 레포지토리 호출 (파라미터 순서 주의!)
	    Page<ItemBoardReply> result = itemBoardReplyRepository.searchAdminReplyList(
	            enabled, searchType, keyword, pageable);

	    // 4. DTO 변환 (regDate는 댓글의 날짜인 r.getRegDate()로 변경!)
	    List<ItemBoardReplyDTO> dtoList = result.getContent().stream()
	            .map(reply -> ItemBoardReplyDTO.builder()
	                    .replyNo(reply.getReplyNo())
	                    .itemId(reply.getItemBoard().getId())
	                    .itemTitle(reply.getItemBoard().getTitle())
	                    .email(reply.getMember().getEmail())
	                    .nickname(reply.getMember().getNickname())
	                    .content(reply.getContent())
	                    .regDate(reply.getRegDate()) // 👈 상품 날짜가 아닌 댓글 날짜!
	                    .parentReplyNo(reply.getParent() != null ? reply.getParent().getReplyNo() : null)
	                    .enabled(reply.getEnabled())
	                    .build())
	            .collect(Collectors.toList());

	    return PageResponseDTO.<ItemBoardReplyDTO>withAll()
	            .pageRequestDTO(searchDTO)
	            .dtoList(dtoList)
	            .totalCount((int) result.getTotalElements())
	            .build();
	}

	@Override
	public Long register(ItemBoardReplyDTO dto) {
		ItemBoard itemBoard = itemBoardRepository.findById(dto.getItemId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다: " + dto.getItemId()));

		Member member = memberRepository.findById(dto.getEmail())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + dto.getEmail()));

		ItemBoardReply parentReply = null;
		if (dto.getParentReplyNo() != null && dto.getParentReplyNo() > 0) {
			parentReply = itemBoardReplyRepository.findById(dto.getParentReplyNo())
					.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 부모 댓글입니다: " + dto.getParentReplyNo()));
		}

		ItemBoardReply reply = ItemBoardReply.builder().itemBoard(itemBoard).member(member).content(dto.getContent())
				.parent(parentReply).enabled(1).build();

		itemBoardReplyRepository.save(reply);

		return reply.getReplyNo();
	}

	@Override
	public void modify(ItemBoardReplyDTO dto) {

		ItemBoardReply reply = itemBoardReplyRepository.findById(dto.getReplyNo()).orElseThrow();

		reply.changeContent(dto.getContent());
	}

	@Override
	public void remove(Long replyNo) {

		ItemBoardReply reply = itemBoardReplyRepository.findById(replyNo).orElseThrow();

		reply.changeEnabled(0);
	}

}
