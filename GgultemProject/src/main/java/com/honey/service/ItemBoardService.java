package com.honey.service;

import com.honey.dto.ItemBoardDTO;
import com.honey.dto.ItemBoardSearchDTO;
import com.honey.dto.PageResponseDTO;

public interface ItemBoardService {

	public ItemBoardDTO get(Long id);

	public Long register(ItemBoardDTO itemBoardDTO);

	PageResponseDTO<ItemBoardDTO> list(ItemBoardSearchDTO searchDTO);

	public void modify(ItemBoardDTO itemBoardDTO);

	public void remove(Long id);

}
